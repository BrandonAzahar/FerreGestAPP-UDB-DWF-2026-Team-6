const API_URL = 'http://localhost:8080/ferregest/api'; // Ajusta la ruta base según tus endpoints de la API

// Elementos de la interfaz
const spinner = document.getElementById('spinner');
const errorDiv = document.getElementById('error-message');

// Función para mostrar/ocultar el spinner de carga
function mostrarCarga(visible) {
    spinner.style.display = visible ? 'block' : 'none';
}

// Función para mostrar mensajes de error
function mostrarError(mensaje) {
    errorDiv.textContent = mensaje;
    setTimeout(() => { errorDiv.textContent = ''; }, 5000); // Se borra a los 5 segundos
}

// 1. MOSTRAR LISTADOS (GET)
async function cargarProductos() {
    mostrarCarga(true);
    errorDiv.textContent = '';
    try {
        const respuesta = await fetch(`${API_URL}/productos`); // Reemplaza con tu endpoint real de productos
        if (!respuesta.ok) throw new Error('Error al conectar con el servidor para obtener productos.');
        
        const productos = await respuesta.json();
        const tbody = document.getElementById('tabla-productos');
        tbody.innerHTML = ''; // Limpiar tabla antes de rellenar

        productos.forEach(prod => {
            const fila = `<tr>
                <td>${prod.id}</td>
                <td>${prod.nombre}</td>
                <td>$${prod.precio}</td>
                <td>${prod.categoria ? prod.categoria.nombre : 'N/A'}</td>
            </tr>`;
            tbody.innerHTML += fila;
        });
    } catch (error) {
        mostrarError(error.message);
    } finally {
        mostrarCarga(false);
    }
}

// 2. ENVIAR DATOS A LA API (POST)
document.getElementById('form-producto').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const nuevoProducto = {
        nombre: document.getElementById('nombre').value,
        precio: parseFloat(document.getElementById('precio').value),
        categoria: { id: parseInt(document.getElementById('categoria_id').value) }
    };

    mostrarCarga(true);
    try {
        const respuesta = await fetch(`${API_URL}/productos`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(nuevoProducto)
        });

        if (!respuesta.ok) throw new Error('No se pudo guardar el producto. Verifique los datos.');

        alert('¡Producto guardado con éxito!');
        document.getElementById('form-producto').reset();
        cargarProductos(); // Recargar la tabla
    } catch (error) {
        mostrarError(error.message);
    } finally {
        mostrarCarga(false);
    }
});

// Cargar los productos automáticamente al abrir la página
window.onload = () => {
    cargarProductos();
};