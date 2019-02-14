package br.com.mauda.seminario.cientificos.junit.executable;

import static br.com.mauda.seminario.cientificos.junit.util.AssertionsMauda.assertEquals;
import static br.com.mauda.seminario.cientificos.junit.util.AssertionsMauda.assertIsNotBlank;
import static br.com.mauda.seminario.cientificos.junit.util.AssertionsMauda.assertNotNull;
import static br.com.mauda.seminario.cientificos.junit.util.AssertionsMauda.assertTrue;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import br.com.mauda.seminario.cientificos.junit.massa.MassaSeminario;
import br.com.mauda.seminario.cientificos.model.AreaCientifica;
import br.com.mauda.seminario.cientificos.model.Inscricao;
import br.com.mauda.seminario.cientificos.model.Professor;
import br.com.mauda.seminario.cientificos.model.Seminario;

public class SeminarioExecutable implements Executable {

    private Seminario seminario;
    private MassaSeminario seminarioEnum;

    public SeminarioExecutable(Seminario seminario) {
        this.seminario = seminario;
    }

    public SeminarioExecutable(Seminario seminario, MassaSeminario enumm) {
        this(seminario);
        this.seminarioEnum = enumm;
    }

    public void basicVerification(Seminario seminario) throws Throwable {
        assertNotNull(seminario, "Um Seminario nao pode ser nulo");
        assertNotNull(seminario.getAreasCientificas(), "É necessário inicializar a lista de areas cientificas");
        assertNotNull(seminario.getInscricoes(), "É necessário inicializar a lista de inscricoes");
        assertNotNull(seminario.getProfessores(), "É necessário inicializar a lista de professores");
        assertNotNull(seminario.getData(), "A data de um Seminario nao pode ser nula");
        assertNotNull(seminario.getQtdInscricoes(), "A quantidade de inscricoes de um Seminario nao pode ser nulo");

        assertTrue(seminario.getQtdInscricoes() > 0, "A quantidade de inscricoes de um Seminario deve ser maior que zero");
        assertIsNotBlank(seminario.getDescricao(), "A descricao de um Seminario nao pode ser nulo ou em branco");
        assertIsNotBlank(seminario.getTitulo(), "O titulo de um Seminario nao pode ser nulo ou em branco");

        for (Professor professor : seminario.getProfessores()) {
            Assertions.assertAll(new ProfessorExecutable(professor));

            // Verifica a associacao bidirecional com professor
            assertTrue(professor.getSeminarios().contains(seminario), "A lista de Seminarios do Professor "
                + professor.getNome() + " nao contem o seminario em questao - associacao bidirecional nao foi realizada");
        }

        for (AreaCientifica area : seminario.getAreasCientificas()) {
            Assertions.assertAll(new AreaCientificaExecutable(area));
        }

        // Verifica se a lista de inscricoes contem a quantidade gerada
        assertEquals(seminario.getQtdInscricoes(), new Integer(seminario.getInscricoes().size()),
            "A lista de inscricoes nao contem todas as inscricoes de acordo com a quantidade estipulada");

        for (Inscricao inscricao : seminario.getInscricoes()) {
            // Verifica a associacao bidirecional com inscricao
            assertEquals(inscricao.getSeminario(), seminario,
                "A inscricao nao contem o seminario em questao - associacao bidirecional nao foi realizada");
        }
    }

    @Override
    public void execute() throws Throwable {
        this.basicVerification(this.seminario);

        if (this.seminarioEnum != null) {
            assertTrue(DateUtils.isSameDay(this.seminarioEnum.getData(), this.seminario.getData()), "Datas dos seminarios nao sao iguais");
            assertEquals(this.seminarioEnum.getDescricao(), this.seminario.getDescricao(), "Descricao dos seminarios nao sao iguais");
            assertEquals(this.seminarioEnum.getQtdInscricoes(), this.seminario.getQtdInscricoes(), "Quantidade de inscricoes nao sao iguais");
            assertEquals(this.seminarioEnum.getTitulo(), this.seminario.getTitulo(), "Titulo dos seminarios nao sao iguais");

            boolean naoAchou = true;
            for (Professor professor : this.seminario.getProfessores()) {
                if (professor.getNome().equals(this.seminarioEnum.getProfessor().getNome())) {
                    Assertions.assertAll(new ProfessorExecutable(professor, this.seminarioEnum.getProfessor()));
                    naoAchou = false;
                    break;
                }
            }
            if (naoAchou) {
                Assertions.fail("Nao encontrou professor correspondente");
            }

            naoAchou = true;
            for (AreaCientifica area : this.seminario.getAreasCientificas()) {
                if (area.getNome().equals(this.seminarioEnum.getAreaCientifica().getNome())) {
                    Assertions.assertAll(new AreaCientificaExecutable(area, this.seminarioEnum.getAreaCientifica()));
                    naoAchou = false;
                    break;
                }
            }
            if (naoAchou) {
                Assertions.fail("Nao encontrou area cientifica correspondente");
            }
            return;
        }
    }
}