const apiBase = "";
const TOKEN_KEY = "pb_token";

function getStoredToken() {
    return window.localStorage.getItem(TOKEN_KEY) || "";
}

function storeToken(token) {
    window.localStorage.setItem(TOKEN_KEY, token);
}

async function fetchStatus(token) {
    try {
        const res = await fetch(apiBase + "/api/status?token=" + encodeURIComponent(token));
        if (!res.ok) {
            throw new Error("加载失败");
        }
        return await res.json();
    } catch (e) {
        console.error(e);
        return null;
    }
}

async function saveConfig(token, behaviorName, stepPercent) {
    const res = await fetch(apiBase + "/api/config?token=" + encodeURIComponent(token), {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            behaviorName,
            stepPercent
        })
    });
    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || "保存失败");
    }
    return res.json();
}

async function clickOnce(token) {
    const res = await fetch(apiBase + "/api/click?token=" + encodeURIComponent(token), {
        method: "POST"
    });
    if (!res.ok) {
        throw new Error("点击失败");
    }
    return res.json();
}

async function registerTokenApi(token) {
    const res = await fetch(apiBase + "/api/token/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({token})
    });
    if (!res.ok) {
        if (res.status === 409) {
            throw new Error("令牌已存在，请换一个。");
        }
        throw new Error("注册失败");
    }
    return res.json();
}

function updateUI(state) {
    if (!state) return;
    const behaviorEl = document.getElementById("currentBehavior");
    const fillEl = document.getElementById("progressFill");
    const labelEl = document.getElementById("progressLabel");
    const countEl = document.getElementById("countInfo");

    behaviorEl.textContent = "当前行为：" + state.behaviorName;

    const percent = Math.max(0, Math.min(100, state.progressPercent || 0));
    fillEl.classList.remove("animate");
    requestAnimationFrame(() => {
        requestAnimationFrame(() => {
            fillEl.style.width = percent + "%";
            fillEl.classList.add("animate");
        });
    });
    labelEl.textContent = percent.toFixed(1).replace(/\.0$/, "") + "%";

    countEl.textContent = "已完成 " + (state.completedCount || 0) + " 次";
}

document.addEventListener("DOMContentLoaded", async () => {
    const loginCard = document.getElementById("loginCard");
    const mainCard = document.getElementById("mainCard");
    const progressCard = document.getElementById("progressCard");
    const helpCard = document.getElementById("helpCard");

    const tokenInput = document.getElementById("tokenInput");
    const loginBtn = document.getElementById("loginBtn");
    const registerBtn = document.getElementById("registerBtn");

    const behaviorInput = document.getElementById("behaviorName");
    const stepInput = document.getElementById("stepPercent");
    const saveConfigBtn = document.getElementById("saveConfigBtn");
    const clickBtn = document.getElementById("clickBtn");

    let currentToken = getStoredToken();
    if (currentToken) {
        tokenInput.value = currentToken;
        try {
            const initialState = await fetchStatus(currentToken);
            if (initialState) {
                behaviorInput.value = initialState.behaviorName || "";
                stepInput.value = initialState.stepPercent || 10;
                updateUI(initialState);
                loginCard.classList.add("hidden");
                mainCard.classList.remove("hidden");
                progressCard.classList.remove("hidden");
                helpCard.classList.remove("hidden");
            }
        } catch (e) {
            console.error(e);
        }
    }

    loginBtn.addEventListener("click", async () => {
        const token = tokenInput.value.trim();
        if (!token) {
            alert("请先输入令牌。");
            return;
        }
        try {
            const state = await fetchStatus(token);
            if (!state) {
                alert("登录失败，请稍后再试。");
                return;
            }
            currentToken = token;
            storeToken(token);
            behaviorInput.value = state.behaviorName || "";
            stepInput.value = state.stepPercent || 10;
            updateUI(state);
            loginCard.classList.add("hidden");
            mainCard.classList.remove("hidden");
            progressCard.classList.remove("hidden");
            helpCard.classList.remove("hidden");
        } catch (e) {
            console.error(e);
            alert("登录失败，请稍后再试。");
        }
    });

    registerBtn.addEventListener("click", async () => {
        const token = tokenInput.value.trim();
        if (!token) {
            alert("请先输入你想设置的令牌。");
            return;
        }
        try {
            const state = await registerTokenApi(token);
            currentToken = token;
            storeToken(token);
            behaviorInput.value = state.behaviorName || "";
            stepInput.value = state.stepPercent || 10;
            updateUI(state);
            loginCard.classList.add("hidden");
            mainCard.classList.remove("hidden");
            progressCard.classList.remove("hidden");
            helpCard.classList.remove("hidden");
            alert("注册并登录成功！");
        } catch (e) {
            console.error(e);
            alert(e.message || "注册失败，请稍后再试。");
        }
    });

    saveConfigBtn.addEventListener("click", async () => {
        if (!currentToken) {
            alert("请先登录令牌。");
            return;
        }
        const name = behaviorInput.value.trim();
        const step = Number(stepInput.value);
        if (!name) {
            alert("请先输入行为名称。");
            return;
        }
        if (!step || step <= 0) {
            alert("每次增加的百分比必须大于 0。");
            return;
        }

        try {
            const newState = await saveConfig(currentToken, name, step);
            updateUI(newState);
            alert("保存成功，进度已重置为 0%。");
        } catch (e) {
            console.error(e);
            alert("保存失败，请稍后重试。");
        }
    });

    clickBtn.addEventListener("click", async () => {
        if (!currentToken) {
            alert("请先登录令牌。");
            return;
        }
        try {
            const newState = await clickOnce(currentToken);
            updateUI(newState);
        } catch (e) {
            console.error(e);
            alert("点击失败，请稍后重试。");
        }
    });
});

