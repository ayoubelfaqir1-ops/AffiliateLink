CREATE TABLE profile_profiles (
                                  id          BIGSERIAL PRIMARY KEY,
                                  email       VARCHAR(255) NOT NULL,
                                  status      VARCHAR(20)  NOT NULL,
                                  created_at  TIMESTAMP    NOT NULL,
                                  CONSTRAINT uk_profile_profiles_email UNIQUE (email),
                                  CONSTRAINT ck_profile_profiles_status
                                      CHECK (status IN ('PENDING', 'ACTIVE', 'SUSPENDED'))
);

CREATE TABLE profile_publishers (
                                    id          BIGINT       PRIMARY KEY,
                                    first_name  VARCHAR(100) NOT NULL,
                                    last_name   VARCHAR(100) NOT NULL,
                                    niche       VARCHAR(20)  NOT NULL,
                                    bio         VARCHAR(500),
                                    country     VARCHAR(100) NOT NULL,
                                    CONSTRAINT fk_publishers_profile
                                        FOREIGN KEY (id) REFERENCES profile_profiles (id),
                                    CONSTRAINT ck_publishers_niche
                                        CHECK (niche IN ('BEAUTY', 'TECH', 'GAMING', 'TRAVEL', 'LIFESTYLE', 'FOOD', 'SPORT'))
);

CREATE TABLE profile_advertisers (
                                     id            BIGINT       PRIMARY KEY,
                                     company_name  VARCHAR(150) NOT NULL,
                                     sector        VARCHAR(100) NOT NULL,
                                     website       VARCHAR(255) NOT NULL,
                                     CONSTRAINT fk_advertisers_profile
                                         FOREIGN KEY (id) REFERENCES profile_profiles (id)
);