-- schema.sql
	
CREATE TABLE patients (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    first_names VARCHAR(100) NOT NULL,
    last_names VARCHAR(100) NOT NULL,
    short_name VARCHAR(100) NOT NULL,
    birth_date DATE NOT NULL,
    sex CHAR(1) NOT NULL,
    is_active BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT '2021-01-01 00:00:00',
    modified_at TIMESTAMP NOT NULL DEFAULT '2021-01-01 00:00:00',
    PRIMARY KEY (id)
);
	
CREATE TABLE sessions (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY,
    themes VARCHAR(100) NOT NULL,
    session_date DATE NOT NULL,
    content TEXT NOT NULL,
    next_week VARCHAR(100),
    is_important BOOLEAN NOT NULL,
    is_paid BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT '2021-01-01 00:00:00',
    modified_at TIMESTAMP NOT NULL DEFAULT '2021-01-01 00:00:00',
    PRIMARY KEY (id)
);
	
CREATE TABLE patient_session (
        patient_id BIGINT,
        session_id BIGINT,
		PRIMARY KEY (patient_id, session_id),
		FOREIGN KEY (patient_id) REFERENCES patients(id),
		FOREIGN KEY (session_id) REFERENCES sessions(id)
);		
	
