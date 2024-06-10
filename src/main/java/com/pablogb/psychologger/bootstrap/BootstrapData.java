package com.pablogb.psychologger.bootstrap;


import com.pablogb.psychologger.domain.entity.PatientEntity;
import com.pablogb.psychologger.domain.entity.SessionEntity;
import com.pablogb.psychologger.domain.entity.Sex;
import com.pablogb.psychologger.repository.PatientRepository;
import com.pablogb.psychologger.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final PatientRepository patientRepository;
    private final SessionRepository sessionRepository;

    @Override
    public void run(String... args) throws Exception {
        PatientEntity patientA = PatientEntity.builder()
                .firstNames("Pablo")
                .lastNames("Garavito Badaracco")
                .shortName("Pablo Garavito")
                .sex(Sex.MALE)
                .birthDate(LocalDate.parse("1987-05-12"))
                .isActive(true)
                .build();

        PatientEntity patientB = PatientEntity.builder()
                .firstNames("Sana")
                .lastNames("Minatozaki")
                .shortName("Sana")
                .sex(Sex.FEMALE)
                .birthDate(LocalDate.parse("1996-12-29"))
                .isActive(true)
                .build();

        SessionEntity sessionA = SessionEntity.builder()
                .sessionDate(LocalDate.parse("2019-05-12"))
                .subject("trabajo niño interior")
                .content("el paciente habló de muchas cosas muy interesantes, ha sufrido mucho en la vida. Le recordé que la vida es un carnaval y que mañana el sol volverá a brillar!!! Así es, amigos, así que ya está en camino a la sanación definitiva de su alma, Dios mediante, claro está =)")
                .isImportant(true)
                .isPaid(true)
                .patients(Set.of(patientA))
                .build();

        SessionEntity sessionB = SessionEntity.builder()
                .sessionDate(LocalDate.parse("2018-08-11"))
                .subject("trabajo emocional")
                .content("la paciente habló de muchas cosas muito interesantes... su situación es complicada, ya que están ocurriendo muchas cosas en su vida. Le recordé que aunque las cosas parezcan muy oscuras, siempre habrá una luz al final del túnel!!! Y al final el verdadero tesoro son los amigos que hicimos en el camion =)")
                .isImportant(true)
                .isPaid(false)
                .nextWeek("situación traumática cuando se le cayó su helado de niño")
                .patients(Set.of(patientB))
                .build();

        SessionEntity sessionAB = SessionEntity.builder()
                .sessionDate(LocalDate.parse("2024-06-03"))
                .subject("sesión pareja")
                .content("se aman mucho, y así será por siempre, al resto de mortales solo nos queda ver y atestiguar quizá el más grande amor nunca antes visto en el mundo moderno, y, tal vez, de todos los tiempos...")
                .isImportant(true)
                .isPaid(false)
                .nextWeek("matrimonio y 9 hijos")
                .patients(Set.of(patientB, patientA))
                .build();

        patientRepository.save(patientA);
        patientRepository.save(patientB);
        sessionRepository.save(sessionA);
        sessionRepository.save(sessionB);
        sessionRepository.save(sessionAB);
    }
}
