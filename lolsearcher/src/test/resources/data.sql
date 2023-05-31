INSERT INTO users (id, email, password, username, role, login_security)
VALUES (1, 'user@naver.com', '$2a$10$T.aFIqEh8NA3QahFugycAu/IZWUXoAihFYYsWwulBZRRkxAg6zUy6', 'user', 'USER', 'NONE'),  --password : 123456789
       (2, 'temporary@naver.com', '$2a$10$T.aFIqEh8NA3QahFugycAu/IZWUXoAihFYYsWwulBZRRkxAg6zUy6', 'temporary', 'TEMPORARY', 'NONE');


INSERT INTO summoner (id, summoner_id, account_id, puuid, summoner_level, summoner_name)
VALUES (1, 'summoner1', 'accountId1', 'puuid1', 10, 'name1'),
       (2, 'summoner2', 'accountId2', 'puuid2', 10, 'name2'),
       (3, 'summoner3', 'accountId3', 'puuid3', 10, 'name3'),
       (4, 'summoner4', 'accountId4', 'puuid4', 10, 'name4'),
       (5, 'summoner5', 'accountId5', 'puuid5', 10, 'name5'),
       (6, 'summoner6', 'accountId6', 'puuid6', 10, 'name6'),
       (7, 'summoner7', 'accountId7', 'puuid7', 10, 'name7'),
       (8, 'summoner8', 'accountId8', 'puuid8', 10, 'name8'),
       (9, 'summoner9', 'accountId9', 'puuid9', 10, 'name9'),
       (10, 'summoner10', 'accountId10', 'puuid10', 10, 'name10');


INSERT INTO matches (id, game_duration, game_end_timestamp, match_id, queue_id, season_id, version)
VALUES (1, '00:20:00', '2022-07-05 00:00:00', 'match1', 1, 22, 'version1');

INSERT INTO match_summary_members (id, match_id, summoner_id, ban_champion_id, pick_champion_id, position_id, champion_level,
minion_kills, kills, assists, deaths, item0, item1, item2, item3, item4, item5, item6, result, team)
VALUES (1, 'match1', 'summoner1', 1, 11, 1, 15, 100, 5, 10, 5, 0,1,2,3,4,5,6, 0, 0),
       (2, 'match1', 'summoner2', 2, 12, 2, 15, 100, 5, 10, 5, 0,1,2,3,4,5,6, 0, 0),
       (3, 'match1', 'summoner3', 3, 13, 3, 15, 100, 5, 10, 5, 0,1,2,3,4,5,6, 0, 0),
       (4, 'match1', 'summoner4', 4, 14, 4, 15, 100, 5, 10, 5, 0,1,2,3,4,5,6, 0, 0),
       (5, 'match1', 'summoner5', 5, 15, 5, 15, 100, 5, 10, 5, 0,1,2,3,4,5,6, 0, 0),
       (6, 'match1', 'summoner6', 6, 16, 1, 13, 100, 2, 6, 5, 0,1,2,3,4,5,6, 1, 1),
       (7, 'match1', 'summoner7', 7, 17, 2, 13, 100, 2, 6, 5, 0,1,2,3,4,5,6, 1, 1),
       (8, 'match1', 'summoner8', 8, 18, 3, 13, 100, 2, 6, 5, 0,1,2,3,4,5,6, 1, 1),
       (9, 'match1', 'summoner9', 9, 19, 4, 13, 100, 2, 6, 5, 0,1,2,3,4,5,6, 1, 1),
       (10, 'match1', 'summoner10', 10, 20, 5, 13, 100, 2, 6, 5, 0,1,2,3,4,5,6, 1, 1);

INSERT INTO match_detail_members (id, summary_member_id, gold_earned, gold_spent, total_damage_dealt, total_damage_dealt_to_champions,
 total_damage_shielded_on_teammates, total_damage_taken, timeccing_others,total_heal,total_heals_on_teammates,
 detector_ward_purchased,detector_wards_placed,ward_kills,wards_placed)
VALUES (1, 1, 10000, 9000, 10000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (2, 2, 10000, 9000, 10000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (3, 3, 10000, 9000, 10000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (4, 4, 10000, 9000, 10000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (5, 5, 10000, 9000, 10000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (6, 6, 6000, 5000, 7000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (7, 7, 6000, 5000, 7000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (8, 8, 6000, 5000, 7000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (9, 9, 6000, 5000, 7000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10),
       (10, 10, 6000, 5000, 7000, 5000, 0, 3000, 13, 2000, 0, 5, 3, 5, 10);

INSERT INTO perks (id, summary_member_id, main_perk_style, sub_perk_style, main_perk1, main_perk2, main_perk3, main_perk4,
sub_perk1, sub_perk2, defense, flex, offense)
VALUES (1, 1, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (2, 2, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (3, 3, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (4, 4, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (5, 5, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (6, 6, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (7, 7, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (8, 8, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (9, 9, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100),
       (10, 10, 1000, 2000, 1, 2, 3, 4, 21, 22, 500, 600, 100);