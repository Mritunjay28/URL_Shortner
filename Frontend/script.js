const API_BASE = "";

const shortenBtn = document.getElementById("shortenBtn");
const copyBtn = document.getElementById("copyBtn");
const statsBtn = document.getElementById("statsBtn");

const resultBox = document.getElementById("resultBox");
const statsBox = document.getElementById("statsBox");
const messageBox = document.getElementById("messageBox");

function showMessage(message, isError = false) {
    messageBox.textContent = message;
    messageBox.classList.remove("hidden");

    if (isError) {
        messageBox.className =
            "p-3 rounded-lg text-center font-medium bg-red-100 text-red-700";
    } else {
        messageBox.className =
            "p-3 rounded-lg text-center font-medium bg-green-100 text-green-700";
    }
}

function clearMessage() {
    messageBox.classList.add("hidden");
}

shortenBtn.addEventListener("click", async () => {
    clearMessage();

    const originalUrl = document.getElementById("originalUrl").value.trim();
    const customCode = document.getElementById("customCode").value.trim();
    const expiryAt = document.getElementById("expiryAt").value;

    const requestBody = {
        originalUrl: originalUrl
    };

    if (customCode) {
        requestBody.customCode = customCode;
    }

    if (expiryAt) {
        requestBody.expiryAt = expiryAt;
    }

    try {
        const response = await fetch(`${API_BASE}/api/urls`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        const data = await response.json();

        if (!response.ok) {
            showMessage(data.message || "Something went wrong", true);
            return;
        }

        document.getElementById("shortUrlOutput").value = data.shortUrl;
        resultBox.classList.remove("hidden");

        showMessage("Short URL created successfully");
    } catch (error) {
        showMessage("Backend connection failed", true);
    }
});

copyBtn.addEventListener("click", async () => {
    const shortUrl = document.getElementById("shortUrlOutput").value;

    await navigator.clipboard.writeText(shortUrl);

    showMessage("Copied to clipboard");
});

statsBtn.addEventListener("click", async () => {
    clearMessage();

    const code = document.getElementById("statsCode").value.trim();

    try {
        const statsResponse = await fetch(`${API_BASE}/${code}/stats`);

        const data = await statsResponse.json();

        if (!statsResponse.ok) {
            showMessage(data.message || "Could not fetch stats", true);
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

        showMessage("Stats fetched successfully");

    } catch (error) {
        showMessage("Backend connection failed", true);
    }
});