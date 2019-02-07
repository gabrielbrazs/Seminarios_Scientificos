package br.com.mauda.seminario.cientificos.junit.tests;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import br.com.mauda.seminario.cientificos.junit.converter.dto.AcaoInscricaoDTOConverter;
import br.com.mauda.seminario.cientificos.junit.dto.AcaoInscricaoDTO;
import br.com.mauda.seminario.cientificos.junit.executable.InscricaoExecutable;
import br.com.mauda.seminario.cientificos.junit.massa.MassaInscricaoCheckIn;
import br.com.mauda.seminario.cientificos.junit.util.MensagensUtils;
import br.com.mauda.seminario.cientificos.model.Inscricao;
import br.com.mauda.seminario.cientificos.model.enums.SituacaoInscricaoEnum;

public class TesteAcaoCheckInSobreInscricao {

    @Tag("modelTest")
    @DisplayName("CheckIn de uma inscricao para o Seminario")
    @ParameterizedTest(name = "CheckIn da inscricao [{arguments}] para o Seminario")
    @EnumSource(MassaInscricaoCheckIn.class)
    public void checkInscricao(@ConvertWith(AcaoInscricaoDTOConverter.class) AcaoInscricaoDTO object) {
        Inscricao inscricao = object.getInscricao();

        // Compra a inscricao pro seminario
        inscricao.comprar(object.getEstudante(), object.getDireitoMaterial());

        this.validarCompra(inscricao);

        // Realiza o check in da inscricao pro seminario
        inscricao.realizarCheckIn();

        // Verifica se os atributos estao preenchidos
        Assertions.assertAll(new InscricaoExecutable(inscricao));

        Assertions.assertNotNull(inscricao.getDataCheckIn(),
            MensagensUtils.getErrorMessage("A data de checkin deve estar preenchida em uma inscricao com situacao checkin"));

        Assertions.assertTrue(DateUtils.isSameDay(inscricao.getDataCheckIn(), new Date()),
            MensagensUtils.getErrorMessage("Data do checkin nao eh igual a hoje"));

        // Verifica se a situacao da inscricao ficou como comprado
        Assertions.assertEquals(inscricao.getSituacao(), SituacaoInscricaoEnum.CHECKIN,
            MensagensUtils.getErrorMessage("Situacao da inscricao nao eh checkIn - trocar a situacao no metodo realizarCheckIn()"));
    }

    private void validarCompra(Inscricao inscricao) {
        // Verifica se os atributos estao preenchidos
        Assertions.assertAll(new InscricaoExecutable(inscricao));

        Assertions.assertNotNull(inscricao.getDataCompra(),
            MensagensUtils.getErrorMessage("A data de compra deve estar preenchida em uma inscricao com situacao comprado"));

        Assertions.assertTrue(DateUtils.isSameDay(inscricao.getDataCompra(), new Date()),
            MensagensUtils.getErrorMessage("Data da compra nao eh igual a hoje"));

        // Verifica se a situacao da inscricao ficou como comprado
        Assertions.assertEquals(inscricao.getSituacao(), SituacaoInscricaoEnum.COMPRADO,
            MensagensUtils.getErrorMessage("Situacao da inscricao nao eh comprado - trocar a situacao no metodo comprar()"));
    }
}