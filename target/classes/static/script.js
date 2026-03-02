const apiBase = "";

async function fetchStatus() {
    try {
        const res = await fetch(apiBase + "/api/status");
        if (!res.ok) {
            throw new Error("加载失败");
        }
        return await res.json();
    } catch (e) {
        console.error(e);
        return null;
    }
}

async function saveConfig(behaviorName, stepPercent) {
    const res = await fetch(apiBase + "/api/config", {
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

async function clickOnce() {
    const res = await fetch(apiBase + "/api/click", {
        method: "POST"
    });
    if (!res.ok) {
        throw new Error("点击失败");
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
    const behaviorInput = document.getElementById("behaviorName");
    const stepInput = document.getElementById("stepPercent");
    const saveConfigBtn = document.getElementById("saveConfigBtn");
    const clickBtn = document.getElementById("clickBtn");

    const initialState = await fetchStatus();
    if (initialState) {
        behaviorInput.value = initialState.behaviorName || "";
        stepInput.value = initialState.stepPercent || 10;
        updateUI(initialState);
    } else {
        document.getElementById("currentBehavior").textContent = "当前行为：网络错误，请稍后重试";
    }

    saveConfigBtn.addEventListener("click", async () => {
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
            const newState = await saveConfig(name, step);
            updateUI(newState);
            alert("保存成功，进度已重置为 0%。");
        } catch (e) {
            console.error(e);
            alert("保存失败，请稍后重试。");
        }
    });

    clickBtn.addEventListener("click", async () => {
        try {
            const newState = await clickOnce();
            updateUI(newState);
        } catch (e) {
            console.error(e);
            alert("点击失败，请稍后重试。");
        }
    });
});

