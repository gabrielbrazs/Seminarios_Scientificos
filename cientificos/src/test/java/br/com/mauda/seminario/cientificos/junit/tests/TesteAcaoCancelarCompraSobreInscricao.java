package br.com.mauda.seminario.cientificos.junit.tests;

import static br.com.mauda.seminario.cientificos.junit.util.AssertionsMauda.assertAll;
import static br.com.mauda.seminario.cientificos.junit.util.AssertionsMauda.assertEquals;
import static br.com.mauda.seminario.cientificos.junit.util.AssertionsMauda.assertThrows;

import java.util.Date;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import br.com.mauda.seminario.cientificos.bc.InscricaoBC;
import br.com.mauda.seminario.cientificos.junit.converter.dao.AcaoInscricaoDTODAOConverter;
import br.com.mauda.seminario.cientificos.junit.converter.dto.AcaoInscricaoDTOConverter;
import br.com.mauda.seminario.cientificos.junit.dto.AcaoInscricaoDTO;
import br.com.mauda.seminario.cientificos.junit.executable.InscricaoExecutable;
import br.com.mauda.seminario.cientificos.junit.massa.MassaInscricaoCancelarCompra;
import br.com.mauda.seminario.cientificos.model.Inscricao;
import br.com.mauda.seminario.cientificos.model.enums.SituacaoInscricaoEnum;
import br.com.mauda.seminario.cientificos.util.EnumUtils;

public class TesteAcaoCancelarCompraSobreInscricao {

    protected InscricaoBC bc = InscricaoBC.getInstance();
    protected AcaoInscricaoDTOConverter converter = new AcaoInscricaoDTOConverter();
    protected AcaoInscricaoDTO acaoInscricaoDTO;

    @BeforeEach
    void beforeEach() {
        this.acaoInscricaoDTO = this.converter.create(EnumUtils.getInstanceRandomly(MassaInscricaoCancelarCompra.class));
    }

    @Tag("queriesDaoTest")
    @DisplayName("Cancelar uma inscricao para o Seminario")
    @ParameterizedTest(name = "Cancelar inscricao [{arguments}] para o Seminario")
    @EnumSource(MassaInscricaoCancelarCompra.class)
    public void cancelarCompra(@ConvertWith(AcaoInscricaoDTODAOConverter.class) AcaoInscricaoDTO object) {
        Inscricao inscricao = object.getInscricao();

        // Realiza o cancelamento da inscricao pro seminario
        this.bc.cancelarCompra(inscricao);

        // Verifica se a inscricao foi removida do estudante
        assertEquals(inscricao.getSituacao(), SituacaoInscricaoEnum.DISPONIVEL,
            "Situacao da inscricao nao eh Disponivel - trocar a situacao no metodo cancelarCompra()");

        // Verifica se os atributos estao preenchidos
        assertAll(new InscricaoExecutable(inscricao));

        // Obtem uma nova instancia do BD a partir do ID gerado
        Inscricao objectBD = this.bc.findById(inscricao.getId());

        assertFalse(objectBD.getEstudante().possuiInscricao(inscricao),
            "Estudante nao deveria possuir a inscricao - remover no metodo cancelarCompra()");

        // Realiza as verificacoes entre o objeto em memoria e o obtido do banco
        assertAll(new InscricaoExecutable(inscricao, objectBD));
    }

    @Tag("queriesDaoTest")
    @Test
    @DisplayName("Cancelar inscricao nula")
    public void validarCompraComInscricaoNula() {
        assertThrows(() -> this.bc.cancelarCompra(null), "ER0003");
    }

    @Tag("queriesDaoTest")
    @Test
    @DisplayName("Cancelar inscricao com a situacao diferente de COMPRADO")
    public void validarCompraComSituacaoInscricaoNaoDisponivel() throws IllegalAccessException {
        Inscricao inscricao = this.acaoInscricaoDTO.getInscricao();

        // Metodo que seta a situacao da inscricao como DISPONIVEL usando reflections
        FieldUtils.writeDeclaredField(inscricao, "situacao", SituacaoInscricaoEnum.DISPONIVEL, true);
        assertThrows(() -> this.bc.cancelarCompra(inscricao), "ER0044");

        // Metodo que seta a situacao da inscricao como CHECKIN usando reflections
        FieldUtils.writeDeclaredField(inscricao, "situacao", SituacaoInscricaoEnum.CHECKIN, true);
        assertThrows(() -> this.bc.cancelarCompra(inscricao), "ER0044");
    }

    @Tag("queriesDaoTest")
    @Test
    @DisplayName("Cancelar compra após a data do Seminario")
    public void validarCancelamentoAposDataSeminario() throws IllegalAccessException {
        Inscricao inscricao = this.acaoInscricaoDTO.getInscricao();

        // Metodo que seta a situacao da inscricao como COMPRADO usando reflections
        FieldUtils.writeDeclaredField(inscricao, "situacao", SituacaoInscricaoEnum.COMPRADO, true);

        // Diminui a data do seminario em 30 dias
        this.acaoInscricaoDTO.getSeminario().setData(DateUtils.addDays(new Date(), -30));
        assertThrows(() -> this.bc.cancelarCompra(inscricao), "ER0045");
    }
}