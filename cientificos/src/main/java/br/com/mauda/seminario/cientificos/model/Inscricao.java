package br.com.mauda.seminario.cientificos.model;

import br.com.mauda.seminario.cientificos.exception.SeminariosCientificosException;
import br.com.mauda.seminario.cientificos.model.enums.SituacaoInscricaoEnum;

public class Inscricao implements DataValidation {

    private Long id;
    private Boolean direitoMaterial;
    private Estudante estudante;
    private Seminario seminario;
    private SituacaoInscricaoEnum situacao;

    public Inscricao(Seminario seminario) {
        this.seminario = seminario;
        this.seminario.adicionarInscricao(this);
        this.situacao = SituacaoInscricaoEnum.DISPONIVEL;
    }

    public void comprar(Estudante estudante, Boolean direitoMaterial) {
        this.situacao = SituacaoInscricaoEnum.COMPRADO;
        this.estudante = estudante;
        this.estudante.adicionarInscricao(this);
        this.direitoMaterial = direitoMaterial;
    }

    public void cancelarCompra() {
        this.situacao = SituacaoInscricaoEnum.DISPONIVEL;
        this.estudante.removerInscricao(this);
        this.estudante = null;
        this.direitoMaterial = null;
    }

    public void realizarCheckIn() {
        this.situacao = SituacaoInscricaoEnum.CHECKIN;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDireitoMaterial() {
        return this.direitoMaterial;
    }

    public void setDireitoMaterial(Boolean direitoMaterial) {
        this.direitoMaterial = direitoMaterial;
    }

    public Estudante getEstudante() {
        return this.estudante;
    }

    public SituacaoInscricaoEnum getSituacao() {
        return this.situacao;
    }

    public Seminario getSeminario() {
        return this.seminario;
    }

    @Override
    public void validateForDataModification() {
        if (this.situacao == null) {
            throw new SeminariosCientificosException("ER0040");
        }

        if (this.seminario == null) {
            throw new SeminariosCientificosException("ER0003");
        }

        if (this.situacao != SituacaoInscricaoEnum.DISPONIVEL) {
            if (this.direitoMaterial == null) {
                throw new SeminariosCientificosException("ER0041");
            }

            if (this.estudante == null) {
                throw new SeminariosCientificosException("ER0003");
            }
        }

        this.estudante.validateForDataModification();

        this.seminario.validateForDataModification();
    }
}
