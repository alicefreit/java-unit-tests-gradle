package com.orangeandbronze.enlistment;

import com.orangeandbronze.enlistment.exceptions.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.*;

import static com.orangeandbronze.enlistment.Days.*;
import static org.junit.jupiter.api.Assertions.*;

public class SectionTest {
    @Test
    public void check_valid_section() {
        try {
            // Criando um período das 08:30 às 10:00
            Period period1 = new Period(830, 1000);

            // Criando um horário nos dias MTH (Segunda, Terça e Quinta)
            Schedule schedule1 = new Schedule(Days.MTH, period1);

            // Criando uma sala com nome "101" e capacidade de 30 alunos
            Room room1 = new Room("101", 30);

            // Criando uma disciplina "MAT101" com 3 unidades (sem pré-requisitos e sem laboratório)
            Subject subject1 = new Subject("MAT101", 3);

            // Criando a seção "SEC101" com os objetos criados acima
            Section section1 = new Section("SEC101", schedule1, room1, subject1);
            Section section8 = new Section("SEC101", schedule1, room1, subject1);
        } catch (Exception e) {
            fail("O teste falhou porque uma exceção foi lançada: " + e.getMessage());
        }
    }

    @Test
    public void check_section_capacity_reached() {
        // Dado um período válido
        Period period1 = new Period(830, 1000);

        // Criando um horário nos dias MTH (Segunda, Terça e Quinta)
        Schedule schedule1 = new Schedule(Days.MTH, period1);

        // Criando uma sala com nome "101" e capacidade de 30 alunos
        Room room1 = new Room("101", 30);

        // Criando uma disciplina "MAT101" com 3 unidades (sem pré-requisitos e sem laboratório)
        Subject subject1 = new Subject("MAT101", 3);

        // Criando a seção "SEC101" com os objetos criados acima
        Section section1 = new Section("SEC101", schedule1, room1, subject1);

        // Simulando a adição de 30 alunos (capacidade máxima)
        for (int i = 0; i < 30; i++) {
            section1.addStudent();
        }

        // Verificando se a capacidade da sala foi atingida
        section1.checkSectionRoomCapacity();  // Verifica se a capacidade foi atingida

        // Quando tentarmos adicionar mais um aluno, a exceção "CapacityReachedException" deve ser lançada
        assertThrows(CapacityReachedException.class, () -> {
            section1.addStudent();  // Tentando adicionar o 31º aluno
        });
    }

    @Test
    public void check_schedule_conflict_between_sections() {
        // Dado dois períodos que se sobrepõem
        Period period1 = new Period(830, 1000);  // Período das 08:30 às 10:00
        Period period2 = new Period(930, 1100);  // Período das 09:30 às 11:00 (sobrepõe-se com o anterior)

        // Criando dois horários nos dias MTH (Segunda, Terça e Quinta)
        Schedule schedule1 = new Schedule(Days.MTH, period1);
        Schedule schedule2 = new Schedule(Days.MTH, period2);

        // Criando duas salas com nome "101" e capacidade de 30 alunos
        Room room1 = new Room("101", 30);
        Room room2 = new Room("102", 30);

        // Criando duas disciplinas "MAT101" com 3 unidades (sem pré-requisitos e sem laboratório)
        Subject subject1 = new Subject("MAT101", 3);
        Subject subject2 = new Subject("MAT102", 3);

        // Criando duas seções "SEC101" e "SEC102"
        Section section1 = new Section("SEC101", schedule1, room1, subject1);
        Section section2 = new Section("SEC102", schedule2, room2, subject2);

        // Quando verificarmos o conflito de horários, a exceção "ScheduleConflictException" deve ser lançada
        assertThrows(ScheduleConflictException.class, () -> {
            section1.checkScheduleConflict(section2);  // Verificando se as seções têm conflito de horário
        });
    }

    @Test
    public void check_same_subject_conflict() {
        // Dado dois períodos diferentes
        Period period1 = new Period(830, 1000);  // Período das 08:30 às 10:00
        Period period2 = new Period(1030, 1200); // Período das 10:30 às 12:00

        // Criando dois horários nos dias MTH (Segunda, Terça e Quinta)
        Schedule schedule1 = new Schedule(Days.MTH, period1);
        Schedule schedule2 = new Schedule(Days.MTH, period2);

        // Criando uma sala com nome "101" e capacidade de 30 alunos
        Room room1 = new Room("101", 30);
        Room room2 = new Room("102", 30);

        // Criando uma disciplina "MAT101" com 3 unidades (sem pré-requisitos e sem laboratório)
        Subject subject1 = new Subject("MAT101", 3);

        // Criando duas seções "SEC101" e "SEC102" com a mesma disciplina
        Section section1 = new Section("SEC101", schedule1, room1, subject1);
        Section section2 = new Section("SEC102", schedule2, room2, subject1); // Mesma disciplina

        // Quando tentarmos verificar o conflito de disciplinas, a exceção "SameSubjectException" deve ser lançada
        assertThrows(SameSubjectException.class, () -> {
            section1.checkForDuplicateSubjects(section2);  // Verificando se as seções possuem a mesma disciplina
        });
    }

    @Test
    public void check_missing_prerequisite() {
        // Dado os períodos válidos para as disciplinas
        Period period1 = new Period(830, 1000);  // Período das 08:30 às 10:00
        Period period2 = new Period(1030, 1200); // Período das 10:30 às 12:00

        // Criando os horários nos dias MTH (Segunda, Terça e Quinta)
        Schedule schedule1 = new Schedule(Days.MTH, period1);
        Schedule schedule2 = new Schedule(Days.MTH, period2);

        // Criando salas e disciplinas
        Room room1 = new Room("11", 30);
        Room room2 = new Room("102", 30);

        Subject subject1 = new Subject("MAT101", 3); // Pre-requisito
        Subject subject2 = new Subject("MAT102", 3); // Depende de MAT101

        // Definindo o pré-requisito para MAT102 (MAT101)
        subject2.getPrerequisites().add(subject1);

        // Criando uma coleção de disciplinas já cursadas (não inclui MAT101)
        Collection<Subject> takenSubjects = new ArrayList<>();

        // Criando a seção "SEC101" para MAT101 e "SEC102" para MAT102
        Section section1 = new Section("SEC101", schedule1, room1, subject1);
        Section section2 = new Section("SEC102", schedule2, room2, subject2);

        // Quando tentarmos verificar os pré-requisitos, a exceção "NotTakenPreRequisiteException" deve ser lançada
        assertThrows(NotTakenPreRequisiteException.class, () -> {
            section2.checkTakenPrerequisite(takenSubjects);  // Verificando se o aluno tem o pré-requisito de MAT101
        });
    }
}
