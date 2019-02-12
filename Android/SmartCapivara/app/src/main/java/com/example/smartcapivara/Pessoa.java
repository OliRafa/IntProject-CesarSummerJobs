package com.example.smartcapivara;

public class Pessoa {
    private String id,setor, motivo, nascimento,
            data_inicio, data_fim, email, nome,
            fone, rg_passaporte, tipo, cargo,
    autorizante, foto;


    public void setID(String newId){
        this.id = newId;
    }

    public void setSetor(String newSetor){
        this.setor = newSetor;
    }

    public void setMotivo(String newMotivo){
        this.motivo = newMotivo;
    }

    public void setNascimento(String newNascimento){
        this.nascimento = newNascimento;
    }

    public void setDataInicio(String newDataInicio){
        this.data_inicio = newDataInicio;
    }

    public void setDataFim(String newDataFim){
        this.data_fim = newDataFim;
    }

    public void setEmail(String newEmail){
        this.email = newEmail;
    }

    public void setNome(String newNome){
        this.nome = newNome;
    }

    public void setFone(String newFone){
        this.fone = newFone;
    }

    public void setRGPassaporte(String newRGPassaporte){
        this.rg_passaporte = newRGPassaporte;
    }

    public void setTipo(String newTipo){
        this.tipo = newTipo;
    }

    public void setAutorizante(String newAutorizante){
        this.autorizante = newAutorizante;
    }

    public void setCargo(String newCargo){
        this.cargo = newCargo;
    }

    public void setFoto(String newFoto){
        this.foto = newFoto;
    }


    public String getID(){
        return this.id;
    }

    public String getSetor(){
        return this.setor;
    }

    public String getMotivo(){
        return this.motivo;
    }

    public String getNascimento(){
        return this.nascimento;
    }

    public String getDataInicio(){
        return this.data_inicio;
    }

    public String getDataFim(){
        return this.data_fim;
    }

    public String getEmail(){
        return this.email;
    }

    public String getNome(){
        return this.nome;
    }

    public String getFone(){
        return this.fone;
    }

    public String getRGPassaporte(){
        return this.rg_passaporte;
    }

    public String getTipo(){
        return this.tipo;
    }

    public String getAutorizante(){ return this.autorizante; }

    public String getCargo(){ return this.cargo; }

    public String getFoto(){
        return this.foto;
    }

    public String toString(){
        String aux;
        aux = "DADOS" + "\n" +
                "Nome: " + this.nome + "\n" +
                "Documento: " + this.rg_passaporte + "\n" +
                "Email: " + this.email + "\n" +
                "ID: " + this.id + "\n" +
                "Tipo: " + this.tipo + "\n";

        return aux;
    }
}
