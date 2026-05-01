/* 后台管理系统 JavaScript */

// API 基础路径
const API_BASE = '/admin';

// 当前状态
let currentPage = 0;
let sessionsPerPage = 10;
let sessionsChart = null;
let messagesChart = null;

// 检查登录状态
function checkLoginStatus() {
    const loggedIn = localStorage.getItem('adminLoggedIn');
    const loginTime = localStorage.getItem('loginTime');

    // 检查是否已登录且登录时间在24小时内
    if (loggedIn === 'true' && loginTime) {
        const loginDate = new Date(loginTime);
        const now = new Date();
        const hoursDiff = (now - loginDate) / (1000 * 60 * 60);

        if (hoursDiff < 24) {
            return true; // 登录有效
        }
    }

    // 清除过期的登录状态
    localStorage.removeItem('adminLoggedIn');
    localStorage.removeItem('adminUsername');
    localStorage.removeItem('loginTime');
    return false;
}

// 初始化：检查登录状态
document.addEventListener('DOMContentLoaded', function() {
    if (!checkLoginStatus()) {
        // 未登录，跳转到登录页面
        window.location.href = '/login.html';
        return;
    }

    // 显示管理员用户名
    const username = localStorage.getItem('adminUsername') || '管理员';
    document.getElementById('adminUsername').textContent = username;

    // 已登录，加载页面数据
    loadDashboardStats();
});

// 页面切换
document.querySelectorAll('.nav-item').forEach(item => {
    item.addEventListener('click', function(e) {
        e.preventDefault();
        const page = this.dataset.page;

        // 更新导航状态
        document.querySelectorAll('.nav-item').forEach(nav => nav.classList.remove('active'));
        this.classList.add('active');

        // 切换页面
        document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
        document.getElementById(`${page}-page`).classList.add('active');

        // 加载页面数据
        if (page === 'dashboard') {
            loadDashboardStats();
        } else if (page === 'sessions') {
            loadSessions();
        } else if (page === 'stats') {
            loadStatsChart();
        }
    });
});

// 加载概览数据
async function loadDashboardStats() {
    try {
        const response = await fetch(`${API_BASE}/stats`);
        const result = await response.json();

        if (result.code === 200) {
            const data = result.data;

            // 更新统计卡片
            document.getElementById('total-sessions').textContent = data.totalSessions || 0;
            document.getElementById('total-messages').textContent = data.totalMessages || 0;
            document.getElementById('active-sessions').textContent = data.activeSessions || 0;
            document.getElementById('total-users').textContent = data.totalUsers || 0;

            // 更新今日数据
            document.getElementById('today-sessions').textContent = data.todaySessions || 0;
            document.getElementById('today-messages').textContent = data.todayMessages || 0;

            // 更新时间
            document.getElementById('update-time').textContent = new Date().toLocaleString('zh-CN');
        }
    } catch (error) {
        console.error('加载统计数据失败:', error);
    }
}

// 加载会话列表
async function loadSessions(page = 0) {
    currentPage = page;
    try {
        const response = await fetch(`${API_BASE}/sessions?page=${page}&size=${sessionsPerPage}`);
        const result = await response.json();

        if (result.code === 200) {
            const sessions = result.data.content;
            const totalPages = result.data.totalPages;

            renderSessionsTable(sessions);
            renderPagination(totalPages, page, loadSessions);
        }
    } catch (error) {
        console.error('加载会话列表失败:', error);
    }
}

// 渲染会话表格
function renderSessionsTable(sessions) {
    const tbody = document.getElementById('sessions-tbody');
    tbody.innerHTML = '';

    if (sessions.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" style="text-align:center;color:#999;">暂无会话数据</td></tr>';
        return;
    }

    sessions.forEach(session => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${session.sessionId.substring(0, 12)}...</td>
            <td>${session.title || '无标题'}</td>
            <td><span class="status-badge status-${session.status}">${session.status === 'active' ? '活跃' : '已关闭'}</span></td>
            <td>-</td>
            <td>${formatDateTime(session.createTime)}</td>
            <td>${formatDateTime(session.updateTime)}</td>
            <td>
                <button class="action-btn btn-view" onclick="viewSession('${session.sessionId}')">查看</button>
                <button class="action-btn btn-delete" onclick="deleteSession('${session.sessionId}')">删除</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// 渲染分页
function renderPagination(totalPages, currentPage, callback) {
    const pagination = document.getElementById('sessions-pagination');
    pagination.innerHTML = '';

    if (totalPages <= 1) return;

    // 上一页
    const prevBtn = document.createElement('button');
    prevBtn.textContent = '上一页';
    prevBtn.disabled = currentPage === 0;
    prevBtn.onclick = () => callback(currentPage - 1);
    pagination.appendChild(prevBtn);

    // 页码
    for (let i = 0; i < totalPages; i++) {
        if (i === currentPage) {
            const activeBtn = document.createElement('button');
            activeBtn.textContent = i + 1;
            activeBtn.classList.add('active');
            pagination.appendChild(activeBtn);
        } else if (i < 3 || i > totalPages - 3 || Math.abs(i - currentPage) < 2) {
            const btn = document.createElement('button');
            btn.textContent = i + 1;
            btn.onclick = () => callback(i);
            pagination.appendChild(btn);
        }
    }

    // 下一页
    const nextBtn = document.createElement('button');
    nextBtn.textContent = '下一页';
    nextBtn.disabled = currentPage === totalPages - 1;
    nextBtn.onclick = () => callback(currentPage + 1);
    pagination.appendChild(nextBtn);
}

// 查看会话详情
async function viewSession(sessionId) {
    try {
        // 获取会话信息
        const sessionResponse = await fetch(`${API_BASE}/sessions/${sessionId}`);
        const sessionResult = await sessionResponse.json();

        // 获取消息列表
        const messagesResponse = await fetch(`${API_BASE}/sessions/${sessionId}/messages`);
        const messagesResult = await messagesResponse.json();

        if (sessionResult.code === 200 && messagesResult.code === 200) {
            const session = sessionResult.data;
            const messages = messagesResult.data;

            // 填充会话信息
            document.getElementById('modal-session-info').innerHTML = `
                <p><strong>会话ID:</strong> ${session.sessionId}</p>
                <p><strong>标题:</strong> ${session.title || '无标题'}</p>
                <p><strong>状态:</strong> ${session.status === 'active' ? '活跃' : '已关闭'}</p>
                <p><strong>创建时间:</strong> ${formatDateTime(session.createTime)}</p>
                <p><strong>更新时间:</strong> ${formatDateTime(session.updateTime)}</p>
            `;

            // 填充消息列表
            const messagesList = document.getElementById('modal-messages');
            messagesList.innerHTML = '';

            messages.forEach(msg => {
                const div = document.createElement('div');
                div.className = `message-item message-${msg.role}`;
                div.innerHTML = `
                    <div class="message-role">${msg.role === 'user' ? '用户' : 'AI助手'}</div>
                    <div class="message-content">${escapeHtml(msg.content)}</div>
                    <div class="message-time">${formatDateTime(msg.createTime)}</div>
                `;
                messagesList.appendChild(div);
            });

            // 显示弹窗
            document.getElementById('messages-modal').classList.add('show');
        }
    } catch (error) {
        console.error('获取会话详情失败:', error);
    }
}

// 关闭弹窗
function closeModal() {
    document.getElementById('messages-modal').classList.remove('show');
}

// 删除会话
async function deleteSession(sessionId) {
    if (!confirm('确定要删除这个会话吗？此操作不可恢复。')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/sessions/${sessionId}`, {
            method: 'DELETE'
        });
        const result = await response.json();

        if (result.code === 200) {
            alert('会话已删除');
            loadSessions(currentPage);
        } else {
            alert('删除失败: ' + result.message);
        }
    } catch (error) {
        console.error('删除会话失败:', error);
        alert('删除失败');
    }
}

// 加载统计图表
async function loadStatsChart() {
    const days = document.getElementById('stats-days').value;

    try {
        const response = await fetch(`${API_BASE}/stats/trend?days=${days}`);
        const result = await response.json();

        if (result.code === 200) {
            const data = result.data;

            // 更新会话趋势图
            renderSessionsChart(data.sessionTrend);

            // 更新消息趋势图
            renderMessagesChart(data.messageTrend);
        }
    } catch (error) {
        console.error('加载统计图表失败:', error);
    }
}

// 渲染会话趋势图
function renderSessionsChart(data) {
    const ctx = document.getElementById('sessions-chart').getContext('2d');

    if (sessionsChart) {
        sessionsChart.destroy();
    }

    sessionsChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map(d => d.date),
            datasets: [{
                label: '会话数',
                data: data.map(d => d.count),
                borderColor: '#667eea',
                backgroundColor: 'rgba(102, 126, 234, 0.1)',
                fill: true,
                tension: 0.4
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// 渲染消息趋势图
function renderMessagesChart(data) {
    const ctx = document.getElementById('messages-chart').getContext('2d');

    if (messagesChart) {
        messagesChart.destroy();
    }

    messagesChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: data.map(d => d.date),
            datasets: [{
                label: '消息数',
                data: data.map(d => d.count),
                backgroundColor: '#764ba2'
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
}

// 格式化日期时间
function formatDateTime(dateTime) {
    if (!dateTime) return '-';
    const date = new Date(dateTime);
    return date.toLocaleString('zh-CN');
}

// HTML转义
function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 搜索功能
document.getElementById('search-input').addEventListener('input', function(e) {
    // 可以添加搜索逻辑
    console.log('搜索:', e.target.value);
});

// 状态筛选
document.getElementById('status-filter').addEventListener('change', function(e) {
    const status = e.target.value;
    if (status === 'active') {
        loadActiveSessions();
    } else {
        loadSessions();
    }
});

// 加载活跃会话
async function loadActiveSessions(page = 0) {
    currentPage = page;
    try {
        const response = await fetch(`${API_BASE}/sessions/active?page=${page}&size=${sessionsPerPage}`);
        const result = await response.json();

        if (result.code === 200) {
            const sessions = result.data.content;
            const totalPages = result.data.totalPages;

            renderSessionsTable(sessions);
            renderPagination(totalPages, page, loadActiveSessions);
        }
    } catch (error) {
        console.error('加载活跃会话失败:', error);
    }
}

// 退出登录
function logout() {
    localStorage.removeItem('adminLoggedIn');
    localStorage.removeItem('adminUsername');
    localStorage.removeItem('loginTime');
    window.location.href = '/login.html';
}