-- Users
INSERT INTO users (name) VALUES ('김철수');
INSERT INTO users (name) VALUES ('이영희');
INSERT INTO users (name) VALUES ('박민준');

-- Chapters
INSERT INTO chapters (name) VALUES ('자료구조 기초');
INSERT INTO chapters (name) VALUES ('알고리즘 기초');

-- Multiple Choice Problems (chapter 1)
INSERT INTO problems (chapter_id, content, solution, problem_type)
VALUES (1, '스택(Stack)의 특성으로 올바른 것을 모두 고르시오.', 'LIFO(Last In First Out) 구조로, 마지막에 삽입된 데이터가 먼저 삭제됩니다. push와 pop 연산을 사용합니다.', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problems (id, choices, correct_answers)
VALUES (1, '["LIFO 구조이다", "FIFO 구조이다", "push 연산으로 삽입한다", "enqueue 연산으로 삽입한다", "pop 연산으로 삭제한다"]', '[1,3,5]');

INSERT INTO problems (chapter_id, content, solution, problem_type)
VALUES (1, '큐(Queue)에 대한 설명으로 올바른 것을 고르시오.', 'FIFO(First In First Out) 구조로, 먼저 삽입된 데이터가 먼저 삭제됩니다.', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problems (id, choices, correct_answers)
VALUES (2, '["LIFO 구조이다", "FIFO 구조이다", "push로 삽입한다", "enqueue로 삽입한다", "스택보다 빠르다"]', '[2,4]');

INSERT INTO problems (chapter_id, content, solution, problem_type)
VALUES (1, '이진 탐색 트리(BST)에서 중위 순회(Inorder Traversal) 결과로 올바른 것을 고르시오.', '이진 탐색 트리를 중위 순회하면 오름차순으로 정렬된 결과를 얻을 수 있습니다.', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problems (id, choices, correct_answers)
VALUES (3, '["내림차순 정렬된 결과", "오름차순 정렬된 결과", "삽입 순서대로 결과", "레벨 순서대로 결과", "역순 결과"]', '[2]');

-- Short Answer Problems (chapter 1)
INSERT INTO problems (chapter_id, content, solution, problem_type)
VALUES (1, '후입선출(Last In First Out) 구조를 갖는 자료구조의 이름을 쓰시오.', '스택은 LIFO 구조로 가장 마지막에 삽입된 데이터가 가장 먼저 삭제되는 자료구조입니다.', 'SHORT_ANSWER');
INSERT INTO short_answer_problems (id, correct_answer)
VALUES (4, '스택');

INSERT INTO problems (chapter_id, content, solution, problem_type)
VALUES (1, '선입선출(First In First Out) 구조를 갖는 자료구조의 이름을 쓰시오.', '큐는 FIFO 구조로 먼저 삽입된 데이터가 먼저 삭제되는 자료구조입니다.', 'SHORT_ANSWER');
INSERT INTO short_answer_problems (id, correct_answer)
VALUES (5, '큐');

-- Multiple Choice Problems (chapter 2)
INSERT INTO problems (chapter_id, content, solution, problem_type)
VALUES (2, '시간 복잡도가 O(n log n)인 정렬 알고리즘을 모두 고르시오.', '병합 정렬과 힙 정렬은 평균 및 최악의 경우 모두 O(n log n)의 시간 복잡도를 가집니다.', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problems (id, choices, correct_answers)
VALUES (6, '["버블 정렬", "선택 정렬", "병합 정렬", "힙 정렬", "삽입 정렬"]', '[3,4]');

INSERT INTO problems (chapter_id, content, solution, problem_type)
VALUES (2, '이진 탐색(Binary Search)의 전제 조건으로 올바른 것을 고르시오.', '이진 탐색은 데이터가 정렬되어 있어야 적용할 수 있습니다.', 'SHORT_ANSWER');
INSERT INTO short_answer_problems (id, correct_answer)
VALUES (7, '정렬');

-- 정답률 계산 테스트를 위한 풀이 이력 (problem 1 기준, 30명 이상)
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (1, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (2, 1, 'WRONG', '1,2');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (3, 1, 'CORRECT', '1,3,5');

-- 더미 유저 풀이 이력 (정답률 계산을 위해 30명 이상 필요)
INSERT INTO users (name) VALUES ('테스트유저04');
INSERT INTO users (name) VALUES ('테스트유저05');
INSERT INTO users (name) VALUES ('테스트유저06');
INSERT INTO users (name) VALUES ('테스트유저07');
INSERT INTO users (name) VALUES ('테스트유저08');
INSERT INTO users (name) VALUES ('테스트유저09');
INSERT INTO users (name) VALUES ('테스트유저10');
INSERT INTO users (name) VALUES ('테스트유저11');
INSERT INTO users (name) VALUES ('테스트유저12');
INSERT INTO users (name) VALUES ('테스트유저13');
INSERT INTO users (name) VALUES ('테스트유저14');
INSERT INTO users (name) VALUES ('테스트유저15');
INSERT INTO users (name) VALUES ('테스트유저16');
INSERT INTO users (name) VALUES ('테스트유저17');
INSERT INTO users (name) VALUES ('테스트유저18');
INSERT INTO users (name) VALUES ('테스트유저19');
INSERT INTO users (name) VALUES ('테스트유저20');
INSERT INTO users (name) VALUES ('테스트유저21');
INSERT INTO users (name) VALUES ('테스트유저22');
INSERT INTO users (name) VALUES ('테스트유저23');
INSERT INTO users (name) VALUES ('테스트유저24');
INSERT INTO users (name) VALUES ('테스트유저25');
INSERT INTO users (name) VALUES ('테스트유저26');
INSERT INTO users (name) VALUES ('테스트유저27');
INSERT INTO users (name) VALUES ('테스트유저28');
INSERT INTO users (name) VALUES ('테스트유저29');
INSERT INTO users (name) VALUES ('테스트유저30');
INSERT INTO users (name) VALUES ('테스트유저31');
INSERT INTO users (name) VALUES ('테스트유저32');
INSERT INTO users (name) VALUES ('테스트유저33');

INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (4, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (5, 1, 'WRONG', '2,4');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (6, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (7, 1, 'WRONG', '1,2,3');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (8, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (9, 1, 'PARTIAL', '1,3');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (10, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (11, 1, 'WRONG', '2,3,4');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (12, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (13, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (14, 1, 'WRONG', '1,2');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (15, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (16, 1, 'PARTIAL', '1,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (17, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (18, 1, 'WRONG', '3,4,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (19, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (20, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (21, 1, 'WRONG', '1,2,3,4,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (22, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (23, 1, 'PARTIAL', '3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (24, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (25, 1, 'WRONG', '2,4');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (26, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (27, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (28, 1, 'WRONG', '1,3');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (29, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (30, 1, 'PARTIAL', '1,3');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (31, 1, 'CORRECT', '1,3,5');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (32, 1, 'WRONG', '2,3');
INSERT INTO user_problem_histories (user_id, problem_id, answer_status, user_answer) VALUES (33, 1, 'CORRECT', '1,3,5');
