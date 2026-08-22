const API_BASE = "";

const shortenBtn = document.getElementById("shortenBtn");
const copyBtn = document.getElementById("copyBtn");
const statsBtn = document.getElementById("statsBtn");

const resultBox = document.getElementById("resultBox");
const statsBox = document.getElementById("statsBox");
const messageBox = document.getElementById("messageBox");
const remainingRequestsEl = document.getElementById("remainingRequests");

let rateLimitTimer = null;

function showMessage(message, type = "success") {
    messageBox.textContent = message;
    messageBox.classList.remove("hidden");

    if (type === "error") {
        messageBox.className =
            "p-3 rounded-lg text-center font-medium bg-red-100 text-red-700";
    } else if (type === "warning") {
        messageBox.className =
            "p-3 rounded-lg text-center font-medium bg-amber-100 text-amber-700";
    } else {
        messageBox.className =
            "p-3 rounded-lg text-center font-medium bg-green-100 text-green-700";
    }
}

function clearMessage() {
    messageBox.classList.add("hidden");
}

function updateRateLimitRemaining(remaining) {
    if (remainingRequestsEl && remaining !== null && remaining !== undefined) {
        remainingRequestsEl.textContent = remaining;
        if (remaining <= 2) {
            remainingRequestsEl.style.color = "#f87171"; // Red if low
        } else if (remaining <= 5) {
            remainingRequestsEl.style.color = "#fbbf24"; // Amber if mid
        } else {
            remainingRequestsEl.style.color = "var(--accent-green)";
        }
    }
}

function handleRateLimit(retrySeconds) {
    let timeLeft = parseInt(retrySeconds, 10) || 120;
    
    if (remainingRequestsEl) {
        remainingRequestsEl.textContent = "0";
        remainingRequestsEl.style.color = "#f87171";
    }

    if (rateLimitTimer) {
        clearInterval(rateLimitTimer);
    }

    shortenBtn.disabled = true;

    const updateCountdown = () => {
        if (timeLeft <= 0) {
            clearInterval(rateLimitTimer);
            shortenBtn.disabled = false;
            shortenBtn.textContent = "Shorten URL →";
            if (remainingRequestsEl) {
                remainingRequestsEl.textContent = "10";
                remainingRequestsEl.style.color = "var(--accent-green)";
            }
            showMessage("Rate limit reset. You can now shorten URLs again!", "success");
        } else {
            shortenBtn.textContent = `⏳ Wait (${timeLeft}s)...`;
            showMessage(`Rate limit reached (10 links/2 min). Try again in ${timeLeft}s.`, "warning");
            timeLeft--;
        }
    };

    updateCountdown();
    rateLimitTimer = setInterval(updateCountdown, 1000);
}

shortenBtn.addEventListener("click", async () => {
    clearMessage();

    const originalUrl = document.getElementById("originalUrl").value.trim();
    const customCode = document.getElementById("customCode").value.trim();
    const expiryAt = document.getElementById("expiryAt").value;

    if (!originalUrl) {
        showMessage("Please enter a valid URL", "error");
        return;
    }

    const requestBody = {
        originalUrl: originalUrl
    };

    if (customCode) {
        requestBody.customCode = customCode;
    }

    if (expiryAt) {
        requestBody.expiryAt = new Date(expiryAt).toISOString();
    }

    try {
        const response = await fetch(`${API_BASE}/api/urls`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        // Check remaining tokens from response header
        const remaining = response.headers.get("X-Rate-Limit-Remaining");
        if (remaining !== null) {
            updateRateLimitRemaining(remaining);
        }

        const data = await response.json();

        // Handle Rate Limit (HTTP 429)
        if (response.status === 429) {
            const retryAfter = response.headers.get("X-Rate-Limit-Retry-After-Seconds") || 120;
            handleRateLimit(retryAfter);
            return;
        }

        if (!response.ok) {
            showMessage(data.message || "Something went wrong", "error");
            return;
        }

        document.getElementById("shortUrlOutput").value = data.shortUrl;
        resultBox.classList.remove("hidden");

        showMessage("Short URL created successfully", "success");
    } catch (error) {
        showMessage("Backend connection failed", "error");
    }
});

copyBtn.addEventListener("click", async () => {
    const shortUrl = document.getElementById("shortUrlOutput").value;

    await navigator.clipboard.writeText(shortUrl);

    showMessage("Copied to clipboard", "success");
});

statsBtn.addEventListener("click", async () => {
    clearMessage();

    const code = document.getElementById("statsCode").value.trim();

    if (!code) {
        showMessage("Please enter a shortcode", "error");
        return;
    }

    try {
        const statsResponse = await fetch(`${API_BASE}/${code}/stats`);

        const data = await statsResponse.json();

        if (!statsResponse.ok) {
            showMessage(data.message || "Could not fetch stats", "error");
            return;
        }

        document.getElementById("statsOriginal").textContent =
            data.originalUrl;

        document.getElementById("statsShortCode").textContent =
            data.shortCode;

        document.getElementById("statsClicks").textContent =
            data.clickCount;

        document.getElementById("statsCreated").textContent =
            data.createdAt;

        statsBox.classList.remove("hidden");

        showMessage("Stats fetched successfully", "success");

    } catch (error) {
        showMessage("Backend connection failed", "error");
    }
});