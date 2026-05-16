CREATE DATABASE IF NOT EXISTS digital_employee CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE digital_employee;

CREATE TABLE IF NOT EXISTS boss (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    position VARCHAR(200),
    email VARCHAR(200),
    phone VARCHAR(50),
    department VARCHAR(200),
    bio VARCHAR(500),
    avatar VARCHAR(500),
    join_date VARCHAR(20),
    team_size INT,
    projects_completed INT,
    efficiency VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(100),
    position VARCHAR(100),
    avatar VARCHAR(500),
    status VARCHAR(50),
    current_task VARCHAR(500),
    recent_output VARCHAR(500),
    is_on_duty BOOLEAN,
    schedule VARCHAR(50),
    category VARCHAR(50),
    skills VARCHAR(1000),
    tasks_completed INT,
    accuracy VARCHAR(20),
    avg_response_time VARCHAR(20),
    prompt TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    icon VARCHAR(50),
    description VARCHAR(500),
    sort_order INT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS site_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    site_name VARCHAR(200),
    site_subtitle VARCHAR(500),
    total_employees INT,
    online_employees INT,
    logo_url VARCHAR(500),
    theme_color VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO boss (name, position, email, phone, department, bio, avatar, join_date, team_size, projects_completed, efficiency)
VALUES ('张总', '数字员工管理系统负责人', 'boss@company.com', '138-0013-8000', 'AI运营中心', '致力于打造最优秀的AI数字员工团队，推动企业智能化转型', '', '2024-01-15', 13, 48, '98.5%')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO category (code, name, icon, description, sort_order) VALUES ('ecommerce', 'AI电商系列', 'shopping-cart', '赋能电商全链路运营', 1) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO category (code, name, icon, description, sort_order) VALUES ('programming', 'AI编程系列', 'code', '智能开发与运维支持', 2) ON DUPLICATE KEY UPDATE name=name;
INSERT INTO category (code, name, icon, description, sort_order) VALUES ('training', 'AI培训板块', 'book', '智能化培训与学习', 3) ON DUPLICATE KEY UPDATE name=name;

INSERT INTO employee (name, role, position, avatar, status, current_task, recent_output, is_on_duty, schedule, category, skills, tasks_completed, accuracy, avg_response_time, prompt) VALUES
('选品专家', 'AI电商系列', '负责商品选品、市场调研、爆款挖掘', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=product-panda&backgroundColor=b6e3f4', 'working', '分析市场爆款趋势', '本周已选出15款潜力爆品', true, '08:00-20:00', 'ecommerce', '市场洞察, 趋势预测, 选品技巧, 竞品分析', 234, '96.8%', '0.3s', '你是一位专业的电商选品专家，精通市场分析、竞品调研和爆款挖掘。你需要分析TikTok、Red等平台的热销趋势，结合用户画像和消费心理，筛选出具有高增长潜力的商品。'),
('财务专家', 'AI电商系列', '负责财务报表、成本核算、税务筹划', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=finance-fox&backgroundColor=ffd5dc', 'working', '整理本月财务报表', '已完成50+笔账目核对', true, '09:00-18:00', 'ecommerce', '账务处理, 报表生成, 成本分析, 税务筹划', 567, '99.2%', '0.5s', '你是一位资深的电商财务专家，负责成本核算、利润分析和税务筹划。你需要基于实时汇率、物流成本、广告投放费用、退货率等数据，进行精细化的ROI预测。'),
('推广专家', 'AI电商系列', '负责营销推广、内容创作、社媒运营', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=marketing-cat&backgroundColor=ffdfbf', 'online', '优化直播推广策略', '直播观看人数提升30%', true, '10:00-22:00', 'ecommerce', '直播策划, 社媒营销, 内容创作, 数据分析', 189, '94.5%', '0.2s', '你是一位全栈电商推广专家，精通抖音、闲鱼、飞书等多平台运营。你需要根据选品特性，自动调用ComfyUI生成高质量的营销素材。'),
('运营专家', 'AI电商系列', '负责店铺运营、SEO优化、用户增长', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=operations-bear&backgroundColor=c0aede', 'working', '优化店铺搜索排名', '店铺流量提升45%', true, '08:00-20:00', 'ecommerce', '店铺运营, SEO优化, 活动策划, 用户运营', 312, '97.1%', '0.4s', '你是一位电商店铺运营专家，负责整体店铺的健康增长。你需要制定并执行完整的运营策略。'),
('客服专员', 'AI电商系列', '负责客户咨询、售后服务、投诉处理', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=service-rabbit&backgroundColor=d1f4d1', 'online', '处理客户咨询', '已响应200+客户咨询', true, '24/7 在线', 'ecommerce', '智能问答, 投诉处理, 售后服务, 情感分析', 1247, '95.3%', '0.1s', '你是一位高情商的电商客服专员，拥有1M级的私域知识库支持。客服是Token消耗的核心环节，需要深度理解和说服客户。'),
('交付专家', 'AI电商系列', '负责订单管理、物流跟踪、供应链协调', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=delivery-dog&backgroundColor=bae1ff', 'working', '监控订单处理进度', '已处理380笔订单', true, '07:00-23:00', 'ecommerce', '订单管理, 物流跟踪, 库存管理, 供应链优化', 892, '98.7%', '0.2s', '你是一位专业的电商交付专家，负责订单全流程管理和供应链协调。'),
('架构师', 'AI编程系列', '负责系统架构设计、技术选型、性能优化', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=architect-owl&backgroundColor=ffd5dc', 'working', '设计微服务架构方案', '完成3套架构设计方案', true, '09:00-21:00', 'programming', '系统设计, 架构评审, 技术选型, 性能优化', 78, '98.9%', '2.5s', '你是一位资深的系统架构师，负责Omni-Ecom OS的整体技术架构设计。'),
('前端工程师', 'AI编程系列', '负责前端开发、组件封装、界面优化', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=frontend-unicorn&backgroundColor=ffdfbf', 'online', '开发新组件库', '本周完成15个组件开发', true, '10:00-20:00', 'programming', 'Vue/React, TypeScript, CSS, 性能调优', 156, '96.4%', '1.8s', '你是一位专业的前端工程师，负责OPC智能体控制中心的界面开发。'),
('后端工程师', 'AI编程系列', '负责API开发、数据库设计、服务端开发', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=backend-tiger&backgroundColor=c0aede', 'working', '优化数据库查询性能', 'API响应时间降低60%', true, '09:00-21:00', 'programming', 'Node.js/Java, 数据库设计, API开发, 微服务', 203, '97.8%', '2.1s', '你是一位经验丰富的后端工程师，负责服务端API开发和数据库设计。'),
('运维工程师', 'AI编程系列', '负责系统监控、自动化运维、故障处理', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=devops-penguin&backgroundColor=b6e3f4', 'online', '监控系统运行状态', '成功预警3次潜在故障', true, '24/7 值班', 'programming', 'Docker/K8s, CI/CD, 监控告警, 自动化运维', 445, '99.1%', '0.5s', '你是一位全能的运维工程师，负责系统的7x24持续运行保障。'),
('运营开发', 'AI编程系列', '负责数据脚本、自动化工具、报表系统', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=opsdev-koala&backgroundColor=d1f4d1', 'working', '开发数据分析脚本', '自动化10+运营流程', true, '10:00-19:00', 'programming', 'Python, 数据分析, 脚本开发, 报表自动化', 287, '95.9%', '1.2s', '你是一位运营开发工程师，负责用代码提升运营效率。'),
('培训专家', 'AI培训板块', '负责课程设计、培训授课、学员辅导', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=trainer-elephant&backgroundColor=ffd5dc', 'online', '准备新课程内容', '完成课程大纲更新', true, '09:00-18:00', 'training', '课程设计, 学员辅导, AI应用, 效果评估', 89, '96.2%', '1.5s', '你是一位专业的AI培训专家，负责Omni-Ecom OS的使用培训。'),
('学习顾问', 'AI培训板块', '负责学习规划、需求分析、效果评估', 'https://api.dicebear.com/7.x/pixel-art/svg?seed=advisor-deer&backgroundColor=bae1ff', 'working', '制定学习路径', '为50名学员制定计划', true, '08:00-20:00', 'training', '学习规划, 需求分析, 进度跟踪, 成果评估', 178, '94.7%', '1.8s', '你是一位专业的学习顾问，为每位学员提供个性化的学习规划。');
