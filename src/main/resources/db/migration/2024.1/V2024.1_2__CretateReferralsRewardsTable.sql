CREATE TABLE wdmc_referrals_rewards (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_key VARCHAR(255) NOT NULL,
  calendar_name VARCHAR(255) NOT NULL,
  calendar_type VARCHAR(255) NOT NULL,
  data JSON NOT NULL,
  claimed_at TIMESTAMP DEFAULT NULL,
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (user_key) REFERENCES wdmc_referrals(user_key)
);
-- -- FILTER BY NAME, USER, CLAIMED
-- SELECT
--    *
-- FROM
--     wdmc_referrals_rewards
-- WHERE
-- 	claimed_at is NULL
--     AND calendar_name = "sysName"
--     AND user_key = 272924120142970892;