UPDATE job_posting
   SET experience_level = 'ANY'
 WHERE source = 'KEAD'
   AND (
       experience_level = '무관'
       OR REPLACE(experience_level, ' ', '') REGEXP '^0년0?개월$'
   );
