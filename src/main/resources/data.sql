-- PATIENT DATA

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (1, 'Pablo', 'Garavito Badaracco', 'Pablo Garavito', '1987-05-12', 'M', TRUE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (2, 'Sana', 'Minatozaki', 'Sana', '1996-12-29', 'F', TRUE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (3, 'Mina', 'Myoui', 'Minita', '1997-03-24', 'F', TRUE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (4, 'Nayeon', 'Im', 'Nayeon', '1995-09-22', 'F', TRUE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (5, 'Tzuyu', 'Chou', 'Tzu', '1999-06-14', 'F', TRUE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (6, 'Airi', 'Suzuki', 'Airi', '1994-04-12', 'F', TRUE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (7, 'Fyodor', 'Mikhailovich Dostoevsky', 'Fyodor Dostoevsky', '1821-10-30', 'M', FALSE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (8, 'Carl Gustav', 'Jung', 'Carl Jung', '1875-07-26', 'M', FALSE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (9, 'Neal', 'Morse', 'Neal Morse', '1960-08-02', 'M', TRUE);

INSERT INTO PATIENT (ID, FIRST_NAMES, LAST_NAMES, SHORT_NAME, BIRTH_DATE, SEX, IS_ACTIVE)
VALUES (10, 'Hermann Karl', 'Hesse', 'Hermann Hesse', '1877-07-02', 'M', TRUE);


-- THERAPY_SESSION DATA
INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(1, 'trabajo niño interior', 'el paciente habló de muchas cosas muy interesantes, ha sufrido mucho en la vida. Le recordé que la vida es un carnaval y que mañana el sol volverá a brillar!!! Así es, amigos, así que ya está en camino a la sanación definitiva de su alma, Dios mediante, claro está =)', '2020-05-12', 'situación traumática cuando se le cayó su helado de niño', TRUE, TRUE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(2, 'trabajo emocional', 'la paciente habló de muchas cosas muito interesantes... su situación es complicada, ya que están ocurriendo muchas cosas en su vida. Le recordé que aunque las cosas parezcan muy oscuras, siempre habrá una luz al final del túnel!!! Y al final el verdadero tesoro son los amigos que hicimos en el camion =)', '2018-08-11', null, TRUE, FALSE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(3, 'sesión de pareja', 'se aman mucho, y así será por siempre, al resto de mortales solo nos queda ver y atestiguar quizá el más grande amor nunca antes visto en el mundo moderno, y, tal vez, de todos los tiempos...', '2024-06-03', 'planes de matrimonio y 9 hijos', TRUE, FALSE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(4, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit.', 'Lorem ipsum aenean nam eros class velit risus quis fermentum consequat, dapibus integer dictum justo aliquet justo placerat velit platea, tempus pellentesque quam quisque donec sagittis pretium varius vivamus. Donec urna maecenas dolor imperdiet litora vehicula, elementum est venenatis porta nec purus porta, massa litora auctor fringilla elit. Himenaeos curabitur sollicitudin orci facilisis nulla justo massa est, ultricies lacinia vitae ornare cubilia himenaeos in feugiat bibendum, placerat maecenas curabitur malesuada faucibus fames interdum', '2020-05-17', null, TRUE, TRUE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(5, 'Lorem ipsum dui fringilla viverra, proin eget elit.', 'Lorem ipsum dictumst posuere pharetra lacus fermentum lacus, et rhoncus habitasse potenti quisque interdum risus, tempus per inceptos urna semper tempor platea, magna ad euismod felis leo venenatis. Primis elit ante velit dictumst lobortis erat, luctus malesuada arcu ultrices sodales donec, posuere dictumst torquent tristique mattis. Hendrerit potenti primis morbi sodales ante sapien mattis nullam, donec elementum nam consectetur pellentesque fames tincidunt eros hac, iaculis id etiam dictum enim tempus litora', '2020-06-12', null, FALSE, FALSE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(6, 'Lorem ipsum netus commodo aliquam, id hendrerit nisl', 'Lorem ipsum consectetur quisque aliquam integer aliquet a metus sollicitudin rutrum, lectus vivamus elit nullam taciti nibh semper gravida lorem semper, eu nisi volutpat ultrices posuere ipsum eleifend duis iaculis. Integer class ullamcorper torquent hac blandit nam sociosqu eros ornare nunc, id torquent feugiat purus magna neque rhoncus nullam ac porttitor, tellus non dapibus eu sem euismod platea nec consequat. Tempus aenean aliquam dictum interdum molestie est consectetur, vivamus in maecenas mollis per platea, urna dictum primis quis posuere tincidunt', '2020-05-12', 'Lorem ipsum sodales tortor, ornare metus', FALSE, TRUE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(7, 'Lorem ipsum nec neque tempus, arcu praesent augue', 'Lorem ipsum nec scelerisque odio accumsan potenti aptent sapien aenean, luctus ullamcorper posuere luctus consectetur elit nullam nostra consectetur, enim leo integer lacinia aliquam velit tempus ad. Semper porttitor justo eros sociosqu habitasse aliquam quisque id lacus, congue ut est consectetur libero in gravida dui, urna curae ad donec aliquam nam iaculis donec. Quisque bibendum placerat tellus class porttitor phasellus eros nulla velit, sollicitudin felis mattis pellentesque per morbi aliquam sapien etiam, iaculis quisque justo fames netus eget lobortis consectetur', '2022-02-12', 'Lorem ipsum elementum donec, sem facilisis', TRUE, FALSE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(8, 'Lorem ipsum felis pretium luctus, phasellus dictumst elementum', 'Lorem ipsum nulla dolor suscipit faucibus metus interdum tempor risus, tempor fringilla condimentum sollicitudin rhoncus elementum litora nisi sit est, scelerisque inceptos libero consequat volutpat curabitur iaculis curabitur. Sociosqu litora volutpat lorem etiam laoreet pellentesque velit ipsum nisi molestie condimentum, posuere taciti tempus fusce nam porttitor est netus placerat duis donec ligula, sollicitudin nunc bibendum viverra vel at etiam nec curae felis. Ac donec aliquam curabitur dapibus vel amet, quis aenean ac convallis vehicula dictum, molestie tortor morbi vulputate duis', '2020-04-30', null, TRUE, TRUE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(9, 'Lorem ipsum ornare quam euismod, adipiscing congue ligula', 'Lorem ipsum congue orci litora volutpat tristique aliquet sit enim hac in, lectus posuere nec lacus sagittis class primis ligula cubilia sagittis. Nisl pulvinar conubia quam morbi inceptos vehicula nam semper, sapien faucibus potenti taciti etiam odio proin ac, dapibus magna elit luctus etiam fermentum arcu. Dapibus sapien id congue pretium nisl habitant erat vestibulum platea euismod diam, pharetra quis nibh sollicitudin duis cursus sed duis suscipit nullam ultrices scelerisque, duis class aliquet nullam sem sed justo libero nisl accumsan', '2024-02-29', null, FALSE, FALSE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(10, 'Lorem ipsum feugiat primis et, non curabitur malesuada', 'Lorem ipsum rhoncus pulvinar lacus habitasse nisl erat velit, adipiscing bibendum nunc feugiat lectus accumsan suspendisse. Aliquet ante pretium tortor nibh fusce morbi eget ipsum, ac nibh sed phasellus pharetra vestibulum hac venenatis congue, semper ornare rutrum sagittis litora a tempus. Phasellus metus lacinia tincidunt vivamus vel non inceptos orci erat dolor, lacinia pharetra quisque tristique a ultricies cras hac potenti. Morbi semper augue etiam donec bibendum posuere, enim vestibulum ullamcorper auctor cursus ligula molestie, porttitor a fusce scelerisque sed', '2011-08-15', null, FALSE, TRUE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(11, 'Lorem ipsum quis per lobortis, tristique fringilla morbi', 'Lorem ipsum mi praesent quisque augue orci augue consequat quisque est molestie eget eros dictum, inceptos nulla posuere blandit metus vel inceptos rhoncus nam nec hendrerit facilisis. Fringilla feugiat quis scelerisque et purus sem consectetur nisl vehicula, hendrerit neque etiam senectus iaculis mi a orci. In donec phasellus hac faucibus sem eu volutpat enim urna, commodo integer tortor id feugiat erat vel erat velit, iaculis aliquet faucibus ultricies metus porttitor varius netus', '2009-05-12', 'Lorem ipsum ac facilisis, pharetra elementum', FALSE, TRUE);

INSERT INTO `SESSION` (ID, THEMES, CONTENT, SESSION_DATE, NEXT_WEEK, IS_IMPORTANT, IS_PAID)
VALUES(12, 'Lorem ipsum cubilia porta aenean, fames platea interdum', 'Lorem ipsum nullam at lorem mauris dolor maecenas ante, ultrices mollis blandit aliquam leo praesent tempus, morbi mauris cras nam per donec fringilla. Blandit et fermentum scelerisque mattis arcu vestibulum quisque purus venenatis commodo justo, dictum laoreet morbi nullam aenean ornare lorem maecenas habitasse nibh. Molestie dapibus imperdiet nibh molestie curabitur est dictum praesent, magna vulputate placerat dictum sagittis odio porta, habitant duis mauris auctor at porta laoreet', '2011-08-11', 'Lorem ipsum ac mattis, magna molestie', TRUE, FALSE);


-- JOIN TABLE DATA
INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (1, 1);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (2, 2);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (1, 3);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (2, 3);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (6, 4);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (7, 5);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (4, 6);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (5, 6);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (1, 7);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (10, 8);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (9, 9);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (1, 10);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (8, 11);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (10, 11);

INSERT INTO PATIENT_SESSION (PATIENT_ID, SESSION_ID)
VALUES (3, 12);