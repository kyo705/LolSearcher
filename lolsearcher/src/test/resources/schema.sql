DROP TABLE users IF EXISTS;
DROP TABLE summoner IF EXISTS;
DROP TABLE ranks IF EXISTS;
DROP TABLE matches IF EXISTS;
DROP TABLE match_summary_members IF EXISTS;
DROP TABLE perks IF EXISTS;
DROP TABLE match_detail_members IF EXISTS;
DROP TABLE most_champs IF EXISTS;

create table users (
    id bigint,
    email varchar(255) not null,
    last_login_time_stamp timestamp,
    login_security varchar(255),
    password VARCHAR(255) not null,
    username varchar(255) not null,
    role varchar(255),
    primary key (id)
);

CREATE TABLE ranks (
	id BIGINT(20) NOT NULL,
	league_id VARCHAR(255) NULL DEFAULT NULL ,
	league_points INT(11) NOT NULL,
	losses BIGINT(20) NOT NULL,
	queue_type INT(11) NULL DEFAULT NULL,
	rank INT(11) NULL DEFAULT NULL,
	season_id INT(11) NOT NULL,
	summoner_id VARCHAR(255) NULL DEFAULT NULL ,
	tier INT(11) NULL DEFAULT NULL,
	wins BIGINT(20) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE summoner (
	id BIGINT(20) NOT NULL,
	account_id VARCHAR(255) NOT NULL ,
	last_match_id VARCHAR(255) NULL DEFAULT NULL  ,
	last_renew_time_stamp DATETIME(6) NULL DEFAULT NULL,
	profile_icon_id INT(11) NULL DEFAULT NULL,
	puuid VARCHAR(255) NOT NULL,
	summoner_id VARCHAR(255) NOT NULL,
	summoner_level BIGINT(20) NOT NULL,
	summoner_name VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

create table matches (
	id bigint not null,
	game_duration TIME not null,
	game_end_timestamp DATETIME not null,
	match_id VARCHAR(255) not null,
	queue_id int not null,
	season_id INT not null,
	version VARCHAR(255) not null,
	primary key (id)
);

CREATE TABLE match_summary_members (
	id BIGINT(20) NOT NULL,
    result INT(11) NOT NULL,
    team INT(11) NOT NULL,
	assists SMALLINT(6) NOT NULL,
	ban_champion_id INT(11) NOT NULL,
	champion_level SMALLINT(6) NOT NULL,
	deaths SMALLINT(6) NOT NULL,
	item0 SMALLINT(6) NOT NULL,
	item1 SMALLINT(6) NOT NULL,
	item2 SMALLINT(6) NOT NULL,
	item3 SMALLINT(6) NOT NULL,
	item4 SMALLINT(6) NOT NULL,
	item5 SMALLINT(6) NOT NULL,
	item6 SMALLINT(6) NOT NULL,
	kills SMALLINT(6) NOT NULL,
	minion_kills SMALLINT(6) NOT NULL,
	pick_champion_id INT(11) NOT NULL,
	position_id SMALLINT(6) NOT NULL,
	summoner_id VARCHAR(255) NOT NULL,
	match_id VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE perks (
	id BIGINT(20) NOT NULL,
	defense SMALLINT(6) NOT NULL,
	flex SMALLINT(6) NOT NULL,
	main_perk1 SMALLINT(6) NOT NULL,
	main_perk2 SMALLINT(6) NOT NULL,
	main_perk3 SMALLINT(6) NOT NULL,
	main_perk4 SMALLINT(6) NOT NULL,
	main_perk_style SMALLINT(6) NOT NULL,
	offense TINYINT(4) NOT NULL,
	sub_perk1 SMALLINT(6) NOT NULL,
	sub_perk2 SMALLINT(6) NOT NULL,
	sub_perk_style SMALLINT(6) NOT NULL,
	summary_member_id BIGINT(20) NOT NULL,
	PRIMARY KEY (id)
);

CREATE TABLE match_detail_members (
	id BIGINT(20) NOT NULL,
	detector_ward_purchased SMALLINT(6) NOT NULL,
	detector_wards_placed SMALLINT(6) NOT NULL,
	gold_earned INT(11) NOT NULL,
	gold_spent INT(11) NOT NULL,
	timeccing_others INT(11) NOT NULL,
	total_damage_dealt INT(11) NOT NULL,
	total_damage_dealt_to_champions INT(11) NOT NULL,
	total_damage_shielded_on_teammates INT(11) NOT NULL,
	total_damage_taken INT(11) NOT NULL,
	total_heal INT(11) NOT NULL,
	total_heals_on_teammates INT(11) NOT NULL,
	ward_kills SMALLINT(6) NOT NULL,
	wards_placed SMALLINT(6) NOT NULL,
	summary_member_id BIGINT(20) NOT NULL,
	PRIMARY KEY (id)
);


CREATE TABLE most_champs (
	id BIGINT(20) NOT NULL,
	champion_id INT(11) NOT NULL,
	queue_id INT(11) NOT NULL,
	season_id INT(11) NOT NULL,
	summoner_id VARCHAR(63) NULL DEFAULT NULL,
	total_assists BIGINT(20) NOT NULL,
	total_deaths BIGINT(20) NOT NULL,
	total_games BIGINT(20) NOT NULL,
	total_kills BIGINT(20) NOT NULL,
	total_losses BIGINT(20) NOT NULL,
	total_minion_kills BIGINT(20) NOT NULL,
	total_wins BIGINT(20) NOT NULL,
	PRIMARY KEY (id)
);