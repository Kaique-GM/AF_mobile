package com.example.af;

public class Medicine {
    private String id;
    private String nome;
    private String descricao;
    private String horario;
    private boolean tomado;

    public Medicine() {}

    public Medicine(String id, String nome, String descricao, String horario, boolean tomado) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.horario = horario;
        this.tomado = tomado;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public String getHorario() { return horario; }
    public boolean isTomado() { return tomado; }

    public void setId(String id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setHorario(String horario) { this.horario = horario; }
    public void setTomado(boolean tomado) { this.tomado = tomado; }
}
