-- Tabulka vyžadovaná Spring Modulith (spring-modulith-starter-jpa)
-- pro persistenci application events

CREATE TABLE event_publication (
    id                      UUID                     NOT NULL PRIMARY KEY,
    listener_id             VARCHAR(512)             NOT NULL,
    event_type              VARCHAR(512)             NOT NULL,
    serialized_event        TEXT                     NOT NULL,
    publication_date        TIMESTAMP WITH TIME ZONE NOT NULL,
    completion_date         TIMESTAMP WITH TIME ZONE,
    last_resubmission_date  TIMESTAMP WITH TIME ZONE,
    completion_attempts     INTEGER                  NOT NULL DEFAULT 0,
    status                  VARCHAR(50)              NOT NULL DEFAULT 'PUBLISHED'
);
