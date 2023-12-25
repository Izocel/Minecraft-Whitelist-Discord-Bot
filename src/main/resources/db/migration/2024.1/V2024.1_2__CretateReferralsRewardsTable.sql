CREATE TABLE wdmc_referrals_rewards (
  id INT AUTO_INCREMENT PRIMARY KEY,
  referral_user_id VARCHAR(255) NOT NULL,
  calendar_name VARCHAR(255) NOT NULL,
  data JSON NOT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  claimed_at TIMESTAMP DEFAULT NULL,
  FOREIGN KEY (referral_user_id) REFERENCES wdmc_referrals(referral_user_id)
);



-- -- FILTER BY NAME, USER, CLAIMED
-- SELECT
--    *
-- FROM
--     wdmc_referrals_rewards
-- WHERE
-- 	claimed_at is NULL
--     AND calendar_name = "sysName"
--     AND referral_user_id = 272924120142970892;