package com.orangeandbronze.enlistment;

import com.orangeandbronze.enlistment.exceptions.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {

    @Test
    public void checkValidSection() {
        // Criando um período das 08:30 às 10:00
        Period period1 = new Period(830, 1000);

        // Criando um horário nos dias MTH (Segunda, Terça e Quinta)
        Schedule schedule1 = new Schedule(Days.MTH, period1);

        // Criando uma sala com nome "101" e capacidade de 30 alunos
        Room room1 = new Room("101", 30);

        // Criando uma disciplina "MAT101" com 3 unidades
        Subject subject1 = new Subject("MAT101", 3);

        // Criando a seção "SEC101" com os objetos criados acima
        assertDoesNotThrow(() -> new Section("SEC101", schedule1, room1, subject1));
    }

      @Test
    public void checkSectionCapacityReached() {
        Period period1 = new Period(830, 1000);
        Schedule schedule1 = new Schedule(Days.MTH, period1);
        Room room1 = new Room("101", 30);
        Subject subject1 = new Subject("MAT101", 3);
        Section section1 = new Section("SEC101", schedule1, room1, subject1);

        // Adicionando 30 alunos (capacidade máxima)
        for (int i = 0; i < 30; i++) {
            section1.addStudent();
        }

        // Verificando a exceção ao tentar adicionar o 31º aluno
        assertThrows(CapacityReachedException.class, section1::addStudent);
    }

   @Test
    public void checkScheduleConflictBetweenSections() {
        Period period1 = new Period(830, 1000);
        Period period2 = new Period(930, 1100); // Conflito
        Schedule schedule1 = new Schedule(Days.MTH, period1);
        Schedule schedule2 = new Schedule(Days.MTH, period2);
        Room room1 = new Room("101", 30);
        Room room2 = new Room("102", 30);
        Subject subject1 = new Subject("MAT101", 3);
        Subject subject2 = new Subject("MAT102", 3);
        Section section1 = new Section("SEC101", schedule1, room1, subject1);
        Section section2 = new Section("SEC102", schedule2, room2, subject2);

        assertThrows(ScheduleConflictException.class, () -> section1.checkScheduleConflict(section2));
    }
    @Test
    public void checkSameSubjectConflict() {
        Period period1 = new Period(830, 1000);
        Period period2 = new Period(1030, 1200);
        Schedule schedule1 = new Schedule(Days.MTH, period1);
        Schedule schedule2 = new Schedule(Days.MTH, period2);
        Room room1 = new Room("101", 30);
        Room room2 = new Room("102", 30);
        Subject subject1 = new Subject("MAT101", 3);
        Section section1 = new Section("SEC101", schedule1, room1, subject1);
        Section section2 = new Section("SEC102", schedule2, room2, subject1);

        assertThrows(SameSubjectException.class, () -> section1.checkForDuplicateSubjects(section2));
    }

    @Test
    public void checkMissingPrerequisite() {
        // Criando períodos de tempo para as seções
        Period period1 = new Period(830, 1000);
        Period period2 = new Period(1030, 1200);

        // Criando horários nos dias MTH
        Schedule schedule1 = new Schedule(Days.MTH, period1);
        Schedule schedule2 = new Schedule(Days.MTH, period2);

        // Criando salas para as seções
        Room room1 = new Room("101", 30);
        Room room2 = new Room("102", 30);

        // Criando disciplinas
        Subject subject1 = new Subject("MAT101", 3);
        Subject subject2 = new Subject("MAT102", 3); // Definindo MAT101 como pré-requisito

        // Criando uma coleção de disciplinas já cursadas (não inclui MAT101)
        Collection<Subject> takenSubjects = new ArrayList<>();

        // Criando as seções para as disciplinas
        Section section1 = new Section("SEC101", schedule1, room1, subject1);
        Section section2 = new Section("SEC102", schedule2, room2, subject2);

        // Quando verificarmos os pré-requisitos, a exceção NotTakenPreRequisiteException deve ser lançada
        assertThrows(NotTakenPreRequisiteException.class, () -> section2.checkTakenPrerequisite(takenSubjects));
    }


}
