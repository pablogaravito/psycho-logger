-- PATIENT DATA

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (1, 'Pablo', 'Garavito Badaracco', 'Pablo Garavito', '1987-05-12', 'M', TRUE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (2, 'Sana', 'Minatozaki', 'Sana', '1996-12-29', 'F', TRUE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (3, 'Mina', 'Myoui', 'Mina', '1997-03-24', 'F', TRUE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (4, 'Nayeon', 'Im', 'Nayeon', '1995-09-22', 'F', TRUE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (5, 'David Michael', 'Buss', 'David Buss', '1953-04-14', 'M', TRUE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (6, 'Airi', 'Suzuki', 'Airi', '1994-04-12', 'F', TRUE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (7, 'Fyodor', 'Mikhailovich Dostoevsky', 'Fyodor Dostoevsky', '1821-10-30', 'M', FALSE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (8, 'Carl Gustav', 'Jung', 'Carl Jung', '1875-07-26', 'M', FALSE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (9, 'Neal', 'Morse', 'Neal Morse', '1960-08-02', 'M', TRUE);

INSERT INTO patients (id, first_names, last_names, short_name, birth_date, sex, is_active)
VALUES (10, 'Hermann Karl', 'Hesse', 'Hermann Hesse', '1877-07-02', 'M', TRUE);

-- SESSION DATA
INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(1, 'trabajo niño interior', 'el paciente habló de muchas cosas muy interesantes, ha sufrido mucho en la vida. Le recordé que la vida es un carnaval y que mañana el sol volverá a brillar!!! Así es, amigos, así que ya está en camino a la sanación definitiva de su alma, Dios mediante, claro está =)', '2020-05-12', 'situación traumática cuando se le cayó su helado de niño', TRUE, TRUE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(2, 'trabajo emocional', 'la paciente habló de muchas cosas muito interesantes... su situación es complicada, ya que están ocurriendo muchas cosas en su vida. Le recordé que aunque las cosas parezcan muy oscuras, siempre habrá una luz al final del túnel!!! Y al final el verdadero tesoro son los amigos que hicimos en el camion =)', '2018-08-11', null, TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(3, 'sesión de pareja', 'se aman mucho, y así será por siempre, al resto de mortales solo nos queda ver y atestiguar quizá el más grande amor nunca antes visto en el mundo moderno, y, tal vez, de todos los tiempos...', '2024-06-03', 'planes de matrimonio y 9 hijos', TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(4, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', 'Lorem ipsum aenean nam eros class velit risus quis fermentum consequat, dapibus integer dictum justo aliquet justo placerat velit platea, tempus pellentesque quam quisque donec sagittis pretium varius vivamus. Donec urna maecenas dolor imperdiet litora vehicula, elementum est venenatis porta nec purus porta, massa litora auctor fringilla elit. Himenaeos curabitur sollicitudin orci facilisis nulla justo massa est, ultricies lacinia vitae ornare cubilia himenaeos in feugiat bibendum, placerat maecenas curabitur malesuada faucibus fames interdum', '2020-05-17', null, TRUE, TRUE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(5, 'Lorem ipsum dui fringilla viverra, proin eget elit.', 'Lorem ipsum dictumst posuere pharetra lacus fermentum lacus, et rhoncus habitasse potenti quisque interdum risus, tempus per inceptos urna semper tempor platea, magna ad euismod felis leo venenatis. Primis elit ante velit dictumst lobortis erat, luctus malesuada arcu ultrices sodales donec, posuere dictumst torquent tristique mattis. Hendrerit potenti primis morbi sodales ante sapien mattis nullam, donec elementum nam consectetur pellentesque fames tincidunt eros hac, iaculis id etiam dictum enim tempus litora', '2020-06-12', null, FALSE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(6, 'Lorem ipsum netus commodo aliquam, id hendrerit nisl', 'Lorem ipsum consectetur quisque aliquam integer aliquet a metus sollicitudin rutrum, lectus vivamus elit nullam taciti nibh semper gravida lorem semper, eu nisi volutpat ultrices posuere ipsum eleifend duis iaculis. Integer class ullamcorper torquent hac blandit nam sociosqu eros ornare nunc, id torquent feugiat purus magna neque rhoncus nullam ac porttitor, tellus non dapibus eu sem euismod platea nec consequat. Tempus aenean aliquam dictum interdum molestie est consectetur, vivamus in maecenas mollis per platea, urna dictum primis quis posuere tincidunt', '2020-05-12', 'Lorem ipsum sodales tortor, ornare metus', FALSE, TRUE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(7, 'Lorem ipsum nec neque tempus, arcu praesent augue', 'Lorem ipsum nec scelerisque odio accumsan potenti aptent sapien aenean, luctus ullamcorper posuere luctus consectetur elit nullam nostra consectetur, enim leo integer lacinia aliquam velit tempus ad. Semper porttitor justo eros sociosqu habitasse aliquam quisque id lacus, congue ut est consectetur libero in gravida dui, urna curae ad donec aliquam nam iaculis donec. Quisque bibendum placerat tellus class porttitor phasellus eros nulla velit, sollicitudin felis mattis pellentesque per morbi aliquam sapien etiam, iaculis quisque justo fames netus eget lobortis consectetur', '2022-02-12', 'Lorem ipsum elementum donec, sem facilisis', TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(8, 'Lorem ipsum felis pretium luctus, phasellus dictumst elementum', 'Lorem ipsum nulla dolor suscipit faucibus metus interdum tempor risus, tempor fringilla condimentum sollicitudin rhoncus elementum litora nisi sit est, scelerisque inceptos libero consequat volutpat curabitur iaculis curabitur. Sociosqu litora volutpat lorem etiam laoreet pellentesque velit ipsum nisi molestie condimentum, posuere taciti tempus fusce nam porttitor est netus placerat duis donec ligula, sollicitudin nunc bibendum viverra vel at etiam nec curae felis. Ac donec aliquam curabitur dapibus vel amet, quis aenean ac convallis vehicula dictum, molestie tortor morbi vulputate duis', '2020-04-30', null, TRUE, TRUE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(9, 'Lorem ipsum ornare quam euismod, adipiscing congue ligula', 'Lorem ipsum congue orci litora volutpat tristique aliquet sit enim hac in, lectus posuere nec lacus sagittis class primis ligula cubilia sagittis. Nisl pulvinar conubia quam morbi inceptos vehicula nam semper, sapien faucibus potenti taciti etiam odio proin ac, dapibus magna elit luctus etiam fermentum arcu. Dapibus sapien id congue pretium nisl habitant erat vestibulum platea euismod diam, pharetra quis nibh sollicitudin duis cursus sed duis suscipit nullam ultrices scelerisque, duis class aliquet nullam sem sed justo libero nisl accumsan', '2024-02-29', null, FALSE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(10, 'Lorem ipsum feugiat primis et, non curabitur malesuada', 'Lorem ipsum rhoncus pulvinar lacus habitasse nisl erat velit, adipiscing bibendum nunc feugiat lectus accumsan suspendisse. Aliquet ante pretium tortor nibh fusce morbi eget ipsum, ac nibh sed phasellus pharetra vestibulum hac venenatis congue, semper ornare rutrum sagittis litora a tempus. Phasellus metus lacinia tincidunt vivamus vel non inceptos orci erat dolor, lacinia pharetra quisque tristique a ultricies cras hac potenti. Morbi semper augue etiam donec bibendum posuere, enim vestibulum ullamcorper auctor cursus ligula molestie, porttitor a fusce scelerisque sed', '2011-08-15', null, FALSE, TRUE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(11, 'Lorem ipsum quis per lobortis, tristique fringilla morbi', 'Lorem ipsum mi praesent quisque augue orci augue consequat quisque est molestie eget eros dictum, inceptos nulla posuere blandit metus vel inceptos rhoncus nam nec hendrerit facilisis. Fringilla feugiat quis scelerisque et purus sem consectetur nisl vehicula, hendrerit neque etiam senectus iaculis mi a orci. In donec phasellus hac faucibus sem eu volutpat enim urna, commodo integer tortor id feugiat erat vel erat velit, iaculis aliquet faucibus ultricies metus porttitor varius netus', '2009-05-12', 'Lorem ipsum ac facilisis, pharetra elementum', FALSE, TRUE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(12, 'Lorem ipsum cubilia porta aenean, fames platea interdum', 'Lorem ipsum nullam at lorem mauris dolor maecenas ante, ultrices mollis blandit aliquam leo praesent tempus, morbi mauris cras nam per donec fringilla. Blandit et fermentum scelerisque mattis arcu vestibulum quisque purus venenatis commodo justo, dictum laoreet morbi nullam aenean ornare lorem maecenas habitasse nibh. Molestie dapibus imperdiet nibh molestie curabitur est dictum praesent, magna vulputate placerat dictum sagittis odio porta, habitant duis mauris auctor at porta laoreet', '2011-08-11', 'Lorem ipsum ac mattis, magna molestie', TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(13, 'Lorem ipsum nulla duis nisi, tristique aenean scelerisque', 'Lorem ipsum porta senectus pulvinar himenaeos cras quisque iaculis imperdiet, gravida torquent tempor phasellus lacinia per lacus semper, ornare phasellus curabitur rhoncus nisl nec lectus elit. Ac hendrerit gravida donec netus augue elit eleifend aliquet donec morbi volutpat odio, orci phasellus fringilla aenean scelerisque fringilla tortor placerat porta in rutrum. Morbi consequat sed habitant pulvinar potenti cubilia nisi curae, euismod bibendum varius class orci torquent inceptos elit vitae, etiam faucibus est mi commodo ornare scelerisque', '2011-08-11', null, TRUE, TRUE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(14, 'Lorem ipsum massa eget neque, molestie platea conubia', 'Lorem ipsum tortor sollicitudin torquent taciti aliquam vel lacinia laoreet turpis purus fringilla, nullam lobortis magna posuere adipiscing nisl porttitor maecenas id sodales nostra. Consequat interdum donec varius lorem id eu urna luctus risus viverra ut, sit enim velit inceptos commodo luctus sagittis hendrerit urna velit. Elit aliquam nibh morbi pretium aptent elit vel sed sodales porttitor risus, faucibus laoreet per mattis risus aliquam tellus nibh vehicula arcu duis nibh, sollicitudin pulvinar iaculis auctor facilisis aliquam vestibulum mattis ullamcorper venenatis', '2012-08-11', 'Lorem ipsum diam, fames', TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(15, 'Lorem ipsum cras enim aptent, vehicula nam eget', 'Lorem ipsum risus posuere porttitor metus aliquam, odio fringilla quisque tempor vestibulum vivamus mauris, urna sem mattis dictum sodales. Convallis facilisis luctus gravida ad varius sed himenaeos orci torquent fringilla magna volutpat vel eleifend ornare, justo cursus eu sit sem torquent curabitur id blandit justo erat quisque tortor. Curabitur elit purus class eleifend amet nisi mauris elit dolor, aliquet interdum velit accumsan varius ornare bibendum vitae, varius blandit bibendum faucibus hendrerit congue placerat aenean', '2013-08-11', 'Lorem ipsum rhoncus, donec', TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(16, 'Lorem ipsum tortor blandit nam, donec iaculis augue', 'Lorem ipsum tempor praesent rhoncus magna primis laoreet nam eget, donec sed dapibus torquent accumsan non proin imperdiet habitasse suspendisse, dictumst nibh aenean scelerisque habitant ultrices rutrum fermentum. Tellus cursus taciti etiam mi nullam curabitur tempus purus proin senectus, aenean suscipit quam porta feugiat urna faucibus accumsan eros, accumsan vitae ornare himenaeos justo habitasse dolor purus vel. Habitasse nullam netus condimentum lobortis velit scelerisque, nullam neque pulvinar justo integer amet euismod, nostra senectus habitasse sed feugiat', '2014-08-11', 'Lorem ipsum felis, est', TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(17, 'Lorem ipsum vel massa netus, laoreet nisl vitae', 'Lorem ipsum eget iaculis per velit libero nulla blandit, nostra ac vitae id curae et tempus malesuada eleifend, quis scelerisque phasellus conubia elementum laoreet praesent. Posuere phasellus aliquam potenti aliquam ultrices, luctus malesuada elit nulla eget nibh, tempor auctor ad erat. Nec imperdiet cras etiam aptent aenean turpis habitant quis, cubilia nostra arcu ultricies vulputate viverra ornare, duis sapien leo massa molestie proin aliquet', '2015-08-11', 'Lorem ipsum quisque, tincidunt', TRUE, FALSE);

INSERT INTO sessions (id, themes, content, session_date, next_week, is_important, is_paid)
VALUES(18, 'Lorem ipsum per dictum est, turpis dolor ullamcorper', 'Lorem ipsum primis risus adipiscing mauris urna quam ipsum, erat per porttitor sollicitudin et arcu leo convallis, ultrices volutpat bibendum eu venenatis erat viverra. Aenean nibh auctor phasellus est tellus condimentum tempus pulvinar ipsum libero diam, arcu interdum leo odio faucibus quisque potenti aenean leo ac. Eleifend nisl class quisque gravida conubia ac leo lectus mattis taciti, porttitor in curabitur torquent aenean dictumst laoreet iaculis in', '2016-08-11', 'Lorem ipsum quis, phasellus', TRUE, FALSE);


-- JOIN TABLE DATA
INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 1);

INSERT INTO patient_session (patient_id, session_id)
VALUES (2, 2);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 3);

INSERT INTO patient_session (patient_id, session_id)
VALUES (2, 3);

INSERT INTO patient_session (patient_id, session_id)
VALUES (6, 4);

INSERT INTO patient_session (patient_id, session_id)
VALUES (7, 5);

INSERT INTO patient_session (patient_id, session_id)
VALUES (4, 6);

INSERT INTO patient_session (patient_id, session_id)
VALUES (5, 6);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 7);

INSERT INTO patient_session (patient_id, session_id)
VALUES (10, 8);

INSERT INTO patient_session (patient_id, session_id)
VALUES (9, 9);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 10);

INSERT INTO patient_session (patient_id, session_id)
VALUES (8, 11);

INSERT INTO patient_session (patient_id, session_id)
VALUES (10, 11);

INSERT INTO patient_session (patient_id, session_id)
VALUES (3, 12);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 13);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 14);

INSERT INTO patient_session (patient_id, session_id)
VALUES (5, 14);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 15);

INSERT INTO patient_session (patient_id, session_id)
VALUES (7, 15);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 16);

INSERT INTO patient_session (patient_id, session_id)
VALUES (8, 16);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 17);

INSERT INTO patient_session (patient_id, session_id)
VALUES (9, 17);

INSERT INTO patient_session (patient_id, session_id)
VALUES (1, 18);

INSERT INTO patient_session (patient_id, session_id)
VALUES (10, 18);