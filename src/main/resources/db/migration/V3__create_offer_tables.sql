-- 1. Table: offer_offres
CREATE TABLE offer_offres (
    id            BIGSERIAL PRIMARY KEY,
    advertiser_id BIGINT       NOT NULL,
    title         VARCHAR(150) NOT NULL,
    description   TEXT,
    landing_url   VARCHAR(255) NOT NULL,
    niche         VARCHAR(50)  NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    CONSTRAINT ck_offer_offres_niche
        CHECK (niche IN ('BEAUTY', 'TECH', 'GAMING', 'TRAVEL', 'LIFESTYLE', 'FOOD', 'SPORT'))
);

CREATE INDEX idx_offer_offres_advertiser_id ON offer_offres (advertiser_id);
CREATE INDEX idx_offer_offres_niche ON offer_offres (niche);

-- 2. Table: offer_tags
CREATE TABLE offer_tags (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT uk_offer_tags_name UNIQUE (name)
);

-- 3. Table: offer_tags_offres (Join Table for Many-to-Many)
CREATE TABLE offer_tags_offres (
    offer_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (offer_id, tag_id),
    CONSTRAINT fk_offer_tags_offres_offer
        FOREIGN KEY (offer_id) REFERENCES offer_offres (id) ON DELETE CASCADE,
    CONSTRAINT fk_offer_tags_offres_tag
        FOREIGN KEY (tag_id) REFERENCES offer_tags (id) ON DELETE CASCADE
);

CREATE INDEX idx_offer_tags_offres_tag_id ON offer_tags_offres (tag_id);

-- 4. Table: offer_campaigns
CREATE TABLE offer_campaigns (
    id           BIGSERIAL PRIMARY KEY,
    offer_id     BIGINT         NOT NULL,
    name         VARCHAR(150)   NOT NULL,
    start_date   DATE           NOT NULL,
    end_date     DATE           NOT NULL,
    budget       NUMERIC(12, 2) NOT NULL,
    payout_model VARCHAR(30)    NOT NULL,
    payout_value NUMERIC(10, 2) NOT NULL,
    status       VARCHAR(20)    NOT NULL,
    CONSTRAINT fk_offer_campaigns_offer
        FOREIGN KEY (offer_id) REFERENCES offer_offres (id) ON DELETE CASCADE,
    CONSTRAINT ck_offer_campaigns_dates
        CHECK (end_date > start_date),
    CONSTRAINT ck_offer_campaigns_budget
        CHECK (budget > 0),
    CONSTRAINT ck_offer_campaigns_status
        CHECK (status IN ('DRAFT', 'ACTIVE', 'PAUSED', 'ENDED')),
    CONSTRAINT ck_offer_campaigns_payout_model
        CHECK (payout_model IN ('CPC', 'CPL', 'CPA_FIXED', 'CPA_PERCENT'))
);

CREATE INDEX idx_offer_campaigns_offer_id ON offer_campaigns (offer_id);
CREATE INDEX idx_offer_campaigns_status ON offer_campaigns (status);

