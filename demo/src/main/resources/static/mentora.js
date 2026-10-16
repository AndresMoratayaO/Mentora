
async function obtenerSesion() {
    try {
        const respuesta = await fetch("/api/sesion");

        if (!respuesta.ok) {
            return null;
        }

        return await respuesta.json();

    } catch (error) {
        return null;
    }
}


async function exigirSesion() {
    const sesion = await obtenerSesion();

    if (!sesion) {
        window.location.replace("iniciosesion.html");
        return null;
    }

    return sesion;
}


async function cerrarSesion() {
    try {
        await fetch("/logout", { method: "POST" });
    } finally {
        window.location.replace("iniciosesion.html");
    }
}

function escaparHTML(valor) {
    const elemento = document.createElement("div");
    elemento.textContent = valor ?? "";
    return elemento.innerHTML;
}


async function leerMensaje(respuesta, respaldo) {
    const tipo = respuesta.headers.get("content-type") || "";

    if (!tipo.includes("text/plain")) {
        return respaldo;
    }

    const texto = (await respuesta.text()).trim();
    return texto || respaldo;
}
