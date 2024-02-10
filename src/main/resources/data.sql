INSERT INTO levels(created_by,level_name) VALUES
                                              ('System', 'Fresher'),
                                              ('System', 'Junior'),
                                              ('System', 'Senior'),
                                              ('System', 'Leader'),
                                              ('System', 'Manager'),
                                              ('System', 'Vice Head');

INSERT INTO benefit(created_by,benefit_name) VALUES
                                                 ('System', 'Lunch'),
                                                 ('System', '25-day leaves'),
                                                 ('System', 'Healthcare insurance'),
                                                 ('System', 'Hybrid working'),
                                                 ('System', 'Travel');

INSERT INTO skills(created_by,skill_name) VALUES
                                              ('System', 'Java'),
                                              ('System', 'Nodejs'),
                                              ('System', '.net'),
                                              ('System', 'C++'),
                                              ('System', 'Business analysis'),
                                              ('System', 'Communication');

INSERT INTO positions(created_by,position_name) VALUES
                                                    ('System','Backend Developer'),
                                                    ('System','Business Analyst'),
                                                    ('System','Tester'),
                                                    ('System','HR'),
                                                    ('System','Project manager'),
                                                    ('System','Not available');

INSERT INTO recruiters(created_by,recruiter_user_name,recruiter_name) VALUES
                                                                          ('System','AnhNTV','Nguyễn Thị Vân Anh'),
                                                                          ('System','XuanDV','Đàm Vĩnh Xuân'),
                                                                          ('System','TriLM','Lưu Minh Trí'),
                                                                          ('System','HoatHT','Hà Thị Hoạt'),
                                                                          ('System','VinhLX','Lưu Xuân Vinh'),
                                                                          ('System','ThuanBN','Bùi Như Thuần');

INSERT INTO candidate_status(created_by,status_name) VALUES
                                                         ('System','Open'),
                                                         ('System','Banned'),
                                                         ('System','Waiting for interview'),
                                                         ('System','Cancelled interview'),
                                                         ('System','Passed Interview'),
                                                         ('System','Failed interview'),
                                                         ('System','Waiting for approval'),
                                                         ('System','Approved offer'),
                                                         ('System','Rejected offer'),
                                                         ('System','Waiting for response'),
                                                         ('System','Accepted offer'),
                                                         ('System','Declined offer'),
                                                         ('System','Cancelled offer');

INSERT INTO highest_level(created_by,highest_level_name) VALUES
                                                             ('System','High school'),
                                                             ('System','Bachelor’s Degree'),
                                                             ('System','Master Degree'),
                                                             ('System','PhD');

INSERT INTO candidates (
    created_by,
    address,
    cv_attachment,
    date_of_birth,
    email,
    full_name,
    gender,
    phone,
    note,
    year_of_experience,
    recruiter_id,
    position_id,
    status_id,
    highest_level_id,
    deleted
) VALUES
      ('System', 'Đống Đa, Hà Nội', 'cv_attachment_template_1', '1990-12-28', 'abc@gmail.com', 'Nguyễn Đăng Vinh', 'MALE', '0987123654', 'note', 1, 1 , 2, 3, 1,FALSE),
      ('System', 'Quận 1, TP Hồ Chí Minh', 'cv_attachment_template_2', '1992-05-15', 'def@gmail.com', 'Nguyễn Thị Mai', 'FEMALE', '0123980654', 'note', 1,  2, 1, 3, 3,FALSE),
      ('System', 'Chí Linh, Hải Dương', 'cv_attachment_template_2', '1997-10-09', 'xyz@gmail.com', 'Nguyễn Danh Thắng', 'MALE', '0912980965', 'note', 1,  3, 3, 3, 2,FALSE),
      ('System', 'Chí Linh, Hải Dương', 'cv_attachment_template_2', '1997-10-09', 'xyzab@gmail.com', 'Nguyễn Hữu Cảnh', 'MALE', '0912980969', 'note', 1,  3, 3, 3, 2,FALSE);

INSERT INTO candidate_skill (candidate_id, skill_id)
VALUES (1, 1), (1, 2), (1, 3),(2,3),(2,4),(2,1),(3,2),(3,3);


INSERT INTO jobs (
    created_by,
    title,
    start_date,
    min_salary,
    max_salary,
    working_address,
    status,
    end_date,
    deleted

) VALUES
      ('System','Fresher Java','2023-12-21',1000.00,2000.00,'Ha Noi','OPEN','2023-12-28',FALSE),
      ('System','Fresher .net','2023-12-21',1000.00,2000.00,'TP HCM','OPEN','2023-12-28',FALSE),
      ('System','Junior Java','2023-12-21',1200.00,2500.00,'TP HCM','OPEN','2023-12-28',FALSE);


INSERT INTO job_skill (job_id, skill_id)
VALUES (1, 1), (1, 2), (1, 3),(2,3),(2,4),(2,1),(3,2),(3,3);

INSERT INTO job_benefit (job_id, benefit_id)
VALUES (1, 1), (1, 2), (1, 3),(2,3),(2,4),(2,1),(3,2),(3,3);

INSERT INTO job_level (job_id, level_id)
VALUES (1, 1), (1, 2), (1, 3),(2,3),(2,4),(2,1),(3,2),(3,3);


INSERT INTO roles (role_name, created_by) VALUES ('RECRUITER', 'SYSTEM'), ('ADMIN', 'SYSTEM'), ('MANAGER', 'SYSTEM'), ('INTERVIEWER', 'SYSTEM');


INSERT INTO department (department_name, created_by) VALUES ('IT', 'SYSTEM'), ('HR', 'SYSTEM'), ('Finance', 'SYSTEM'), ('Commucation', 'SYSTEM'), ('Marketing', 'SYSTEM'), ('Accounting', 'SYSTEM');

INSERT INTO users (full_name, username, password, email, date_of_birth, address, phone_number, gender, status, note, department_id, position_id, created_by)
VALUES ('Bùi Như Thuần', 'recruiter', '$2a$10$HMt3Mu5BUjBGFtKaOhNnJeOfwzzhAM0u5/G5rjU4n97dF7vdI0A/a', 'recruiter@gmail.com', '1990-01-01', 'Hanoi', '1234567890', 'MALE', 'ACTIVE', 'Dep trai vo doi', 1, 1, 'SYSTEM'),
       ('Lưu Xuân Vinh', 'admin', '$2a$10$8rJzffvjA5fE5Ll8p1deDuvvdSVr9rAZRkj2nuAZNSC9teC1pjhCO', 'admin@gmail.com', '1980-02-02', 'Hue', '9876543210', 'FEMALE', 'ACTIVE', 'Dep trai vo doi', 2, 2, 'SYSTEM'),
       ('Nguyen Van Tho', 'manager', '$2a$10$9PRqBy9Q1236DUrZECUINO1Cp1YNxGBqwiYWyF3zJUjmMrLGi8V/.', 'only.thonguyenvan@gmail.com', '1970-03-03', 'Saigon', '1112233445', 'MALE', 'ACTIVE', 'Dep trai vo doi', 3, 3, 'SYSTEM'),
       ('Lưu Minh Trí', 'interviewer', '$2a$10$nuOUDlRwDle.6kzN.oJRkeWdPi6SGQdZzeGWqFf2nor7NLYjn2Fe.', 'interviewer@gmail.com', '1995-04-04', 'Phu Quoc', '5432109876', 'FEMALE', 'ACTIVE', 'Dep trai vo doi', 4, 4, 'SYSTEM'),
       ('Đàm Vĩnh Xuân', 'ex-manager', '$2a$10$yBiK2mnIvw5e6fB4NQ2nAOZfpuRtv0iD1F36z3ahlF1m77cCH2HNy', 'ex-manager@gmail.com', '1970-03-03', 'Danang', '1231231230', 'MALE', 'INACTIVE', 'Dep trai vo doi', 3, 5, 'SYSTEM');

INSERT INTO user_roles (user_id, role_id) VALUES (1,1), (2,2), (3,3), (4,4), (5,2);

INSERT INTO interviewer (interviewer_name, created_by,email) VALUES ('HanhNT12', 'SYSTEM','ducthole21@gmail.com'), ('HoangNV4', 'SYSTEM','abc@gmail.com'), ('ThuyNT4', 'SYSTEM','def@gmail.com'), ('NgaTT45', 'SYSTEM','xyz@gmail.com');

INSERT INTO interview (
    created_by,
    schedule_title,
    interview_date,
    start_time,
    end_time,
    notes,
    location,
    meeting_id,
    result,
    status,
    candidate_id,
    job_id,
    recruiter_id,
    deleted
) VALUES ('System','Interview Junior Java Dev','2024-01-05','12:00:00','14:00:00','N/A','Ha Noi','meet.google.com/123','Passed','New',1,2,3,0),
         ('System','Interview Senior Java Dev','2024-01-05','12:00:00','14:00:00','N/A','Ha Noi','meet.google.com/678','Passed','New',3,1,2,0),
         ('System','Interview Fresher .net Dev','2024-01-05','12:00:00','14:00:00','N/A','Ha Noi','meet.google.com/345','Passed','New',2,3,1,0);

INSERT INTO interview_interviewer(interview_id, interviewer_id) VALUES (1,1), (2,2), (3,3);

INSERT INTO offer (candidate_id, position_id, approved_by, interview_id, contract_period_from, contract_period_to, interview_notes, contract_type, level_id, offer_status, department_id, recruiter_owner_id, due_date, basic_salary, note, created_by,deleted)
VALUES (1, 1, 1, 1, '2024-01-01', '2024-12-31', 'Good performance', 'FULL_TIME', 1, 'APPROVED_OFFER', 1, 1, '2024-02-01', 50000.00, 'Offer ngon vcl', 'SYSTEM',0),
       (2, 2, 2, 2, '2024-01-01', '2024-12-31', 'Average performance', 'PART_TIME', 2, 'CANCELLED', 2, 2, '2024-02-01', 32000.00, 'Offer ngon vcl', 'SYSTEM',0),
       (3, 3, 3, 3, '2024-01-01', '2024-12-31', 'Excellent performance', 'CONTRACTOR', 3, 'APPROVED_OFFER', 3, 3, '2024-02-01', 89000.00, 'Offer ngon vcl', 'SYSTEM',0);

