// 登录页面 JavaScript

document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const loginBtn = document.getElementById('loginBtn');
    const errorMessage = document.getElementById('errorMessage');
    const errorText = document.getElementById('errorText');

    // 表单提交处理
    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        // 获取输入值
        const username = usernameInput.value.trim();
        const password = passwordInput.value;

        // 基本验证
        if (!username || !password) {
            showError('请输入账号和密码');
            return;
        }

        // 隐藏错误信息
        hideError();

        // 显示加载状态
        setLoading(true);

        try {
            // 调用登录接口
            const response = await fetch('/admin/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: username,
                    password: password
                })
            });

            const result = await response.json();

            if (result.code === 200 && result.data && result.data.success) {
                // 登录成功，保存登录状态
                localStorage.setItem('adminLoggedIn', 'true');
                localStorage.setItem('adminUsername', username);
                localStorage.setItem('loginTime', new Date().toISOString());

                // 跳转到后台管理页面
                window.location.href = '/admin/index.html';
            } else {
                // 登录失败
                showError(result.message || '账号或密码错误');
                setLoading(false);
            }
        } catch (error) {
            console.error('登录请求失败:', error);
            showError('网络错误，请稍后重试');
            setLoading(false);
        }
    });

    // 输入框变化时隐藏错误信息
    usernameInput.addEventListener('input', hideError);
    passwordInput.addEventListener('input', hideError);

    // 显示错误信息
    function showError(message) {
        errorText.textContent = message;
        errorMessage.style.display = 'flex';
        usernameInput.parentElement.classList.add('error');
        passwordInput.parentElement.classList.add('error');
    }

    // 隐藏错误信息
    function hideError() {
        errorMessage.style.display = 'none';
        usernameInput.parentElement.classList.remove('error');
        passwordInput.parentElement.classList.remove('error');
    }

    // 设置加载状态
    function setLoading(loading) {
        loginBtn.disabled = loading;
        loginBtn.querySelector('.btn-text').style.display = loading ? 'none' : 'inline';
        loginBtn.querySelector('.btn-loading').style.display = loading ? 'flex' : 'none';
    }
});