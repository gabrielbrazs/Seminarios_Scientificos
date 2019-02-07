package br.com.mauda.seminario.cientificos.junit.executable;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import br.com.mauda.seminario.cientificos.junit.massa.MassaEstudante;
import br.com.mauda.seminario.cientificos.junit.util.MensagensUtils;
import br.com.mauda.seminario.cientificos.model.Estudante;

public class EstudanteExecutable implements Executable {

    private Estudante estudante;
    private MassaEstudante estudanteEnum;

    public EstudanteExecutable(Estudante estudante) {
        this.estudante = estudante;
    }

    public EstudanteExecutable(Estudante estudante, MassaEstudante enumm) {
        this(estudante);
        this.estudanteEnum = enumm;
    }

    public void basicVerification(Estudante estudante) throws Throwable {
        Assertions.assertNotNull(estudante, MensagensUtils.getErrorMessage("Um Estudante nao pode ser nulo"));

        Assertions.assertNotNull(estudante.getInscricoes(), MensagensUtils.getErrorMessage("É necessário inicializar a lista de inscricoes"));

        Assertions.assertTrue(StringUtils.isNotBlank(estudante.getEmail()),
            MensagensUtils.getErrorMessage("O email de um Estudante nao pode ser nulo ou em branco"));

        Assertions.assertTrue(StringUtils.isNotBlank(estudante.getNome()),
            MensagensUtils.getErrorMessage("O nome de um Estudante nao pode ser nulo ou em branco"));

        Assertions.assertTrue(StringUtils.isNotBlank(estudante.getTelefone()),
            MensagensUtils.getErrorMessage("O telefone de um Estudante nao pode ser nulo ou em branco"));

        // Verifica se a instituicao dentro do estudante esta preenchida corretamente
        Assertions.assertAll(new InstituicaoExecutable(estudante.getInstituicao()));
    }

    @Override
    public void execute() throws Throwable {
        this.basicVerification(this.estudante);

        if (this.estudanteEnum != null) {
            Assertions.assertEquals(this.estudanteEnum.getEmail(), this.estudante.getEmail(),
                MensagensUtils.getErrorMessage("Emails dos estudantes nao sao iguais"));

            Assertions.assertEquals(this.estudanteEnum.getNome(), this.estudante.getNome(),
                MensagensUtils.getErrorMessage("Nomes dos estudantes nao sao iguais"));

            Assertions.assertEquals(this.estudanteEnum.getTelefone(), this.estudante.getTelefone(),
                MensagensUtils.getErrorMessage("Telefones dos estudantes nao sao iguais"));

            Assertions.assertAll(new InstituicaoExecutable(this.estudante.getInstituicao(), this.estudanteEnum.getInstituicao()));
            return;
        }
    }
}
