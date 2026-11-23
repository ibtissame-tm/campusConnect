// Afficher / masquer le mot de passe
const input  = document.getElementById("password");
const toggle = document.getElementById("togglePassword");

toggle.addEventListener("click", () => {
    const hidden = input.type === "password";
    input.type = hidden ? "text" : "password";
    toggle.textContent = hidden ? "Masquer" : "Afficher";
    toggle.setAttribute("aria-label", hidden ? "Masquer le mot de passe" : "Afficher le mot de passe");
});