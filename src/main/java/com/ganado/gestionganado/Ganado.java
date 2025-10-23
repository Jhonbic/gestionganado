/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.ganado.gestionganado;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ganado")
public class Ganado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String raza;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String fotoUrl;

    @Column(length = 500)
    private String notas;

    // Relaciones autoreferenciadas (padres)
    @ManyToOne
    @JoinColumn(name = "padre_id")
    private Ganado padre;

    @ManyToOne
    @JoinColumn(name = "madre_id")
    private Ganado madre;

    // Constructores
    public Ganado() {
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public Ganado getPadre() {
        return padre;
    }

    public void setPadre(Ganado padre) {
        this.padre = padre;
    }

    public Ganado getMadre() {
        return madre;
    }

    public void setMadre(Ganado madre) {
        this.madre = madre;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

}
