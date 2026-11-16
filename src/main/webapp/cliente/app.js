/**
 * FerreGest - Cliente Externo Independiente (Fase 3 - Sprint 3)
 * Consume la API REST (/api/categorias y /api/productos) mediante Fetch API (Vanilla JS).
 * Gestiona tablas dinámicas, formularios POST/PUT, eliminaciones DELETE,
 * spinners de carga y alertas de error/éxito en la interfaz.
 */

// Resolución automática del contexto de despliegue (ej. "/ferregest" en Tomcat)
const CONTEXT_PATH = window.location.pathname.includes('/cliente')
    ? window.location.pathname.split('/cliente')[0]
    : '';

const API_CATEGORIAS = `${CONTEXT_PATH}/api/categorias`;
const API_PRODUCTOS = `${CONTEXT_PATH}/api/productos`;

// Estado local sincronizado con la API REST
let estadoCategorias = [];
let estadoProductos = [];

// Referencias del DOM
const dom = {
    globalSpinner: document.getElementById('globalSpinner'),
    globalSpinnerText: document.getElementById('globalSpinnerText'),
    alertContainer: document.getElementById('alertContainer'),
    btnRecargarTodo: document.getElementById('btnRecargarTodo'),

    // KPIs
    kpiTotalProductos: document.getElementById('kpiTotalProductos'),
    kpiTotalCategorias: document.getElementById('kpiTotalCategorias'),
    kpiStockTotal: document.getElementById('kpiStockTotal'),
    kpiValorInventario: document.getElementById('kpiValorInventario'),

    // Formulario y tabla de Productos
    formProducto: document.getElementById('formProducto'),
    productoId: document.getElementById('productoId'),
    productoNombre: document.getElementById('productoNombre'),
    productoCategoria: document.getElementById('productoCategoria'),
    productoPrecio: document.getElementById('productoPrecio'),
    productoStock: document.getElementById('productoStock'),
    productoDescripcion: document.getElementById('productoDescripcion'),
    tituloFormProducto: document.getElementById('tituloFormProducto'),
    badgeModoProducto: document.getElementById('badgeModoProducto'),
    btnGuardarProducto: document.getElementById('btnGuardarProducto'),
    spinnerBtnProducto: document.getElementById('spinnerBtnProducto'),
    iconoBtnProducto: document.getElementById('iconoBtnProducto'),
    textoBtnProducto: document.getElementById('textoBtnProducto'),
    btnCancelarProducto: document.getElementById('btnCancelarProducto'),
    spinnerProductos: document.getElementById('spinnerProductos'),
    contenedorTablaProductos: document.getElementById('contenedorTablaProductos'),
    tablaProductosBody: document.getElementById('tablaProductosBody'),
    filtroCategoriaProductos: document.getElementById('filtroCategoriaProductos'),
    inputBuscarProducto: document.getElementById('inputBuscarProducto'),

    // Formulario y tabla de Categorías
    formCategoria: document.getElementById('formCategoria'),
    categoriaId: document.getElementById('categoriaId'),
    categoriaNombre: document.getElementById('categoriaNombre'),
    categoriaDescripcion: document.getElementById('categoriaDescripcion'),
    tituloFormCategoria: document.getElementById('tituloFormCategoria'),
    badgeModoCategoria: document.getElementById('badgeModoCategoria'),
    btnGuardarCategoria: document.getElementById('btnGuardarCategoria'),
    spinnerBtnCategoria: document.getElementById('spinnerBtnCategoria'),
    iconoBtnCategoria: document.getElementById('iconoBtnCategoria'),
    textoBtnCategoria: document.getElementById('textoBtnCategoria'),
    btnCancelarCategoria: document.getElementById('btnCancelarCategoria'),
    spinnerCategorias: document.getElementById('spinnerCategorias'),
    contenedorTablaCategorias: document.getElementById('contenedorTablaCategorias'),
    tablaCategoriasBody: document.getElementById('tablaCategoriasBody')
};

// =========================================================================
// Inicialización de la aplicación
// =========================================================================
document.addEventListener('DOMContentLoaded', () => {
    registrarEventos();
    cargarDatosIniciales();
});

function registrarEventos() {
    dom.btnRecargarTodo.addEventListener('click', () => cargarDatosIniciales());
    dom.formProducto.addEventListener('submit', procesarFormularioProducto);
    dom.btnCancelarProducto.addEventListener('click', resetearFormularioProducto);
    dom.formCategoria.addEventListener('submit', procesarFormularioCategoria);
    dom.btnCancelarCategoria.addEventListener('click', resetearFormularioCategoria);

    dom.filtroCategoriaProductos.addEventListener('change', filtrarYRenderizarProductos);
    dom.inputBuscarProducto.addEventListener('input', filtrarYRenderizarProductos);
}

/**
 * Carga simultáneamente categorías (/api/categorias) y productos (/api/productos)
 */
async function cargarDatosIniciales() {
    mostrarSpinnerGlobal(true, 'Sincronizando inventario con la API REST...');
    try {
        await obtenerCategorias();
        await obtenerProductos();
        actualizarKPIs();
    } catch (error) {
        mostrarAlerta(`Error al sincronizar con la API REST: ${error.message}`, 'danger');
    } finally {
        mostrarSpinnerGlobal(false);
    }
}

// =========================================================================
// Consumo de /api/categorias con Fetch API
// =========================================================================

async function obtenerCategorias() {
    mostrarSpinnerSeccion(dom.spinnerCategorias, dom.contenedorTablaCategorias, true);
    try {
        const response = await fetch(API_CATEGORIAS, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        const data = await parsearRespuestaJSON(response);

        if (!response.ok) {
            throw new Error(extraerMensajeError(data, response.status));
        }

        estadoCategorias = Array.isArray(data) ? data : [];
        poblarSelectoresCategorias();
        renderizarTablaCategorias(estadoCategorias);
        actualizarKPIs();
    } catch (error) {
        mostrarAlerta(`No se pudieron cargar las categorías (/api/categorias): ${error.message}`, 'danger');
        renderizarTablaCategorias([]);
    } finally {
        mostrarSpinnerSeccion(dom.spinnerCategorias, dom.contenedorTablaCategorias, false);
    }
}

async function procesarFormularioCategoria(event) {
    event.preventDefault();

    const idEdicion = dom.categoriaId.value.trim();
    const nombre = dom.categoriaNombre.value.trim();
    const descripcion = dom.categoriaDescripcion.value.trim();

    if (!nombre) {
        mostrarAlerta('El nombre de la categoría es obligatorio y no puede estar vacío.', 'warning');
        dom.categoriaNombre.focus();
        return;
    }

    const payload = {
        nombre: nombre,
        descripcion: descripcion || null
    };

    const esEdicion = idEdicion !== '';
    const url = esEdicion ? `${API_CATEGORIAS}/${idEdicion}` : API_CATEGORIAS;
    const metodo = esEdicion ? 'PUT' : 'POST';

    cambiarEstadoBotonCategoria(true);
    mostrarSpinnerGlobal(true, esEdicion ? 'Actualizando categoría...' : 'Registrando nueva categoría...');

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const data = await parsearRespuestaJSON(response);

        if (!response.ok) {
            throw new Error(extraerMensajeError(data, response.status));
        }

        mostrarAlerta(
            esEdicion
                ? `Categoría "${data.nombre}" actualizada correctamente (HTTP ${response.status}).`
                : `Categoría "${data.nombre}" creada exitosamente (HTTP ${response.status}).`,
            'success'
        );

        resetearFormularioCategoria();
        await obtenerCategorias();
        await obtenerProductos();
    } catch (error) {
        mostrarAlerta(`Error al guardar categoría: ${error.message}`, 'danger');
    } finally {
        cambiarEstadoBotonCategoria(false);
        mostrarSpinnerGlobal(false);
    }
}

function prepararEdicionCategoria(id) {
    const categoria = estadoCategorias.find(c => c.id === id);
    if (!categoria) {
        mostrarAlerta(`No se encontró la categoría #${id} en memoria.`, 'warning');
        return;
    }

    dom.categoriaId.value = categoria.id;
    dom.categoriaNombre.value = categoria.nombre || '';
    dom.categoriaDescripcion.value = categoria.descripcion || '';

    dom.tituloFormCategoria.innerHTML = `<i class="bi bi-pencil-square text-warning me-1"></i> Editar Categoría #${categoria.id}`;
    dom.badgeModoCategoria.textContent = 'PUT';
    dom.badgeModoCategoria.className = 'badge bg-warning text-dark';
    dom.textoBtnCategoria.textContent = 'Actualizar Categoría';
    dom.btnCancelarCategoria.classList.remove('d-none');

    dom.categoriaNombre.focus();
}

async function eliminarCategoria(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar la categoría "${nombre}" (ID: ${id})?\nLos productos asociados quedarán como "Sin Categoría".`)) {
        return;
    }

    mostrarSpinnerGlobal(true, `Eliminando categoría #${id}...`);
    try {
        const response = await fetch(`${API_CATEGORIAS}/${id}`, {
            method: 'DELETE',
            headers: { 'Accept': 'application/json' }
        });

        const data = await parsearRespuestaJSON(response);

        if (!response.ok) {
            throw new Error(extraerMensajeError(data, response.status));
        }

        mostrarAlerta(data.mensaje || `Categoría "${nombre}" eliminada correctamente (HTTP ${response.status}).`, 'success');

        if (dom.categoriaId.value === String(id)) {
            resetearFormularioCategoria();
        }

        await obtenerCategorias();
        await obtenerProductos();
    } catch (error) {
        mostrarAlerta(`Error al eliminar categoría: ${error.message}`, 'danger');
    } finally {
        mostrarSpinnerGlobal(false);
    }
}

function resetearFormularioCategoria() {
    dom.formCategoria.reset();
    dom.categoriaId.value = '';
    dom.tituloFormCategoria.innerHTML = '<i class="bi bi-plus-circle text-warning me-1"></i> Nueva Categoría';
    dom.badgeModoCategoria.textContent = 'POST';
    dom.badgeModoCategoria.className = 'badge bg-primary';
    dom.textoBtnCategoria.textContent = 'Guardar Categoría';
    dom.btnCancelarCategoria.classList.add('d-none');
}

// =========================================================================
// Consumo de /api/productos con Fetch API
// =========================================================================

async function obtenerProductos() {
    mostrarSpinnerSeccion(dom.spinnerProductos, dom.contenedorTablaProductos, true);
    try {
        const response = await fetch(API_PRODUCTOS, {
            method: 'GET',
            headers: { 'Accept': 'application/json' }
        });

        const data = await parsearRespuestaJSON(response);

        if (!response.ok) {
            throw new Error(extraerMensajeError(data, response.status));
        }

        estadoProductos = Array.isArray(data) ? data : [];
        filtrarYRenderizarProductos();
        actualizarKPIs();
    } catch (error) {
        mostrarAlerta(`No se pudieron cargar los productos (/api/productos): ${error.message}`, 'danger');
        renderizarTablaProductos([]);
    } finally {
        mostrarSpinnerSeccion(dom.spinnerProductos, dom.contenedorTablaProductos, false);
    }
}

async function procesarFormularioProducto(event) {
    event.preventDefault();

    const idEdicion = dom.productoId.value.trim();
    const nombre = dom.productoNombre.value.trim();
    const categoriaVal = dom.productoCategoria.value;
    const precioVal = parseFloat(dom.productoPrecio.value);
    const stockVal = parseInt(dom.productoStock.value, 10);
    const descripcion = dom.productoDescripcion.value.trim();

    if (!nombre) {
        mostrarAlerta('El nombre del producto es obligatorio y no puede estar vacío.', 'warning');
        dom.productoNombre.focus();
        return;
    }

    if (Number.isNaN(precioVal) || precioVal <= 0) {
        mostrarAlerta('El precio del producto debe ser un número mayor que cero.', 'warning');
        dom.productoPrecio.focus();
        return;
    }

    if (Number.isNaN(stockVal) || stockVal < 0) {
        mostrarAlerta('El stock del producto debe ser un número entero mayor o igual a cero.', 'warning');
        dom.productoStock.focus();
        return;
    }

    const payload = {
        nombre: nombre,
        descripcion: descripcion || null,
        precio: precioVal,
        stock: stockVal,
        categoriaId: categoriaVal ? parseInt(categoriaVal, 10) : null
    };

    const esEdicion = idEdicion !== '';
    const url = esEdicion ? `${API_PRODUCTOS}/${idEdicion}` : API_PRODUCTOS;
    const metodo = esEdicion ? 'PUT' : 'POST';

    cambiarEstadoBotonProducto(true);
    mostrarSpinnerGlobal(true, esEdicion ? 'Actualizando producto...' : 'Registrando nuevo producto...');

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        const data = await parsearRespuestaJSON(response);

        if (!response.ok) {
            throw new Error(extraerMensajeError(data, response.status));
        }

        mostrarAlerta(
            esEdicion
                ? `Producto "${data.nombre}" actualizado correctamente (HTTP ${response.status}).`
                : `Producto "${data.nombre}" creado exitosamente (HTTP ${response.status}).`,
            'success'
        );

        resetearFormularioProducto();
        await obtenerProductos();
    } catch (error) {
        mostrarAlerta(`Error al guardar producto: ${error.message}`, 'danger');
    } finally {
        cambiarEstadoBotonProducto(false);
        mostrarSpinnerGlobal(false);
    }
}

function prepararEdicionProducto(id) {
    const producto = estadoProductos.find(p => p.id === id);
    if (!producto) {
        mostrarAlerta(`No se encontró el producto #${id} en memoria.`, 'warning');
        return;
    }

    dom.productoId.value = producto.id;
    dom.productoNombre.value = producto.nombre || '';
    dom.productoCategoria.value = producto.categoriaId || '';
    dom.productoPrecio.value = Number(producto.precio).toFixed(2);
    dom.productoStock.value = producto.stock;
    dom.productoDescripcion.value = producto.descripcion || '';

    dom.tituloFormProducto.innerHTML = `<i class="bi bi-pencil-square text-warning me-1"></i> Editar Producto #${producto.id}`;
    dom.badgeModoProducto.textContent = 'PUT';
    dom.badgeModoProducto.className = 'badge bg-warning text-dark';
    dom.textoBtnProducto.textContent = 'Actualizar Producto';
    dom.btnCancelarProducto.classList.remove('d-none');

    dom.productoNombre.focus();
}

async function eliminarProducto(id, nombre) {
    if (!confirm(`¿Está seguro de eliminar el producto "${nombre}" (ID: ${id})?`)) {
        return;
    }

    mostrarSpinnerGlobal(true, `Eliminando producto #${id}...`);
    try {
        const response = await fetch(`${API_PRODUCTOS}/${id}`, {
            method: 'DELETE',
            headers: { 'Accept': 'application/json' }
        });

        const data = await parsearRespuestaJSON(response);

        if (!response.ok) {
            throw new Error(extraerMensajeError(data, response.status));
        }

        mostrarAlerta(data.mensaje || `Producto "${nombre}" eliminado correctamente (HTTP ${response.status}).`, 'success');

        if (dom.productoId.value === String(id)) {
            resetearFormularioProducto();
        }

        await obtenerProductos();
    } catch (error) {
        mostrarAlerta(`Error al eliminar producto: ${error.message}`, 'danger');
    } finally {
        mostrarSpinnerGlobal(false);
    }
}

function resetearFormularioProducto() {
    dom.formProducto.reset();
    dom.productoId.value = '';
    dom.tituloFormProducto.innerHTML = '<i class="bi bi-plus-circle text-warning me-1"></i> Nuevo Producto';
    dom.badgeModoProducto.textContent = 'POST';
    dom.badgeModoProducto.className = 'badge bg-primary';
    dom.textoBtnProducto.textContent = 'Guardar Producto';
    dom.btnCancelarProducto.classList.add('d-none');
}

// =========================================================================
// Renderizado de Tablas Dinámicas y KPIs
// =========================================================================

function poblarSelectoresCategorias() {
    const valorActualForm = dom.productoCategoria.value;
    const valorActualFiltro = dom.filtroCategoriaProductos.value;

    dom.productoCategoria.innerHTML = '<option value="">-- Sin Categoría --</option>';
    dom.filtroCategoriaProductos.innerHTML = '<option value="">Todas las categorías</option>';

    estadoCategorias.forEach(cat => {
        const opcionForm = document.createElement('option');
        opcionForm.value = cat.id;
        opcionForm.textContent = cat.nombre;
        dom.productoCategoria.appendChild(opcionForm);

        const opcionFiltro = document.createElement('option');
        opcionFiltro.value = cat.id;
        opcionFiltro.textContent = cat.nombre;
        dom.filtroCategoriaProductos.appendChild(opcionFiltro);
    });

    dom.productoCategoria.value = valorActualForm;
    dom.filtroCategoriaProductos.value = valorActualFiltro;
}

function filtrarYRenderizarProductos() {
    const catFiltro = dom.filtroCategoriaProductos.value;
    const textoFiltro = dom.inputBuscarProducto.value.trim().toLowerCase();

    const filtrados = estadoProductos.filter(prod => {
        const coincideCategoria = !catFiltro || String(prod.categoriaId) === String(catFiltro);
        const coincideTexto = !textoFiltro
            || (prod.nombre && prod.nombre.toLowerCase().includes(textoFiltro))
            || (prod.descripcion && prod.descripcion.toLowerCase().includes(textoFiltro));
        return coincideCategoria && coincideTexto;
    });

    renderizarTablaProductos(filtrados);
}

function renderizarTablaProductos(productos) {
    dom.tablaProductosBody.innerHTML = '';

    if (!productos || productos.length === 0) {
        dom.tablaProductosBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-4 text-muted">
                    <i class="bi bi-inbox fs-4 d-block mb-1"></i>
                    No se encontraron productos registrados.
                </td>
            </tr>
        `;
        return;
    }

    productos.forEach(prod => {
        const tr = document.createElement('tr');
        const nombreCategoria = obtenerNombreCategoria(prod.categoriaId);
        const badgeStockClass = prod.stock <= 5
            ? 'bg-danger-subtle text-danger border border-danger-subtle'
            : 'bg-success-subtle text-success border border-success-subtle';

        tr.innerHTML = `
            <td class="fw-bold text-secondary">#${prod.id}</td>
            <td>
                <div class="fw-semibold text-dark">${escaparHTML(prod.nombre)}</div>
                <div class="small text-muted">${escaparHTML(prod.descripcion || 'Sin descripción')}</div>
            </td>
            <td>
                <span class="badge bg-light text-dark border">
                    <i class="bi bi-tag me-1 text-warning"></i>${escaparHTML(nombreCategoria)}
                </span>
            </td>
            <td class="text-end fw-bold">$${Number(prod.precio || 0).toFixed(2)}</td>
            <td class="text-center">
                <span class="badge ${badgeStockClass} px-2 py-1">${prod.stock} uds.</span>
            </td>
            <td class="text-center">
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-outline-primary btn-editar-prod" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-outline-danger btn-eliminar-prod" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;

        tr.querySelector('.btn-editar-prod').addEventListener('click', () => prepararEdicionProducto(prod.id));
        tr.querySelector('.btn-eliminar-prod').addEventListener('click', () => eliminarProducto(prod.id, prod.nombre));

        dom.tablaProductosBody.appendChild(tr);
    });
}

function renderizarTablaCategorias(categorias) {
    dom.tablaCategoriasBody.innerHTML = '';

    if (!categorias || categorias.length === 0) {
        dom.tablaCategoriasBody.innerHTML = `
            <tr>
                <td colspan="4" class="text-center py-4 text-muted">
                    <i class="bi bi-inbox fs-4 d-block mb-1"></i>
                    No hay categorías registradas.
                </td>
            </tr>
        `;
        return;
    }

    categorias.forEach(cat => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="fw-bold text-secondary">#${cat.id}</td>
            <td class="fw-semibold text-dark">${escaparHTML(cat.nombre)}</td>
            <td class="text-muted">${escaparHTML(cat.descripcion || 'Sin descripción')}</td>
            <td class="text-center">
                <div class="btn-group btn-group-sm" role="group">
                    <button type="button" class="btn btn-outline-primary btn-editar-cat" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button type="button" class="btn btn-outline-danger btn-eliminar-cat" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        `;

        tr.querySelector('.btn-editar-cat').addEventListener('click', () => prepararEdicionCategoria(cat.id));
        tr.querySelector('.btn-eliminar-cat').addEventListener('click', () => eliminarCategoria(cat.id, cat.nombre));

        dom.tablaCategoriasBody.appendChild(tr);
    });
}

function actualizarKPIs() {
    dom.kpiTotalProductos.textContent = estadoProductos.length;
    dom.kpiTotalCategorias.textContent = estadoCategorias.length;

    const stockTotal = estadoProductos.reduce((acc, p) => acc + (Number(p.stock) || 0), 0);
    const valorTotal = estadoProductos.reduce((acc, p) => acc + ((Number(p.precio) || 0) * (Number(p.stock) || 0)), 0);

    dom.kpiStockTotal.textContent = stockTotal;
    dom.kpiValorInventario.textContent = `$${valorTotal.toFixed(2)}`;
}

// =========================================================================
// Utilidades de UI: Spinners de Carga, Alertas de Error y Helpers
// =========================================================================

function mostrarSpinnerGlobal(visible, texto = 'Consultando API REST...') {
    if (visible) {
        dom.globalSpinnerText.textContent = texto;
        dom.globalSpinner.classList.remove('d-none');
        dom.globalSpinner.classList.add('d-flex');
    } else {
        dom.globalSpinner.classList.add('d-none');
        dom.globalSpinner.classList.remove('d-flex');
    }
}

function mostrarSpinnerSeccion(spinnerEl, contenedorEl, cargando) {
    if (cargando) {
        spinnerEl.classList.remove('d-none');
        contenedorEl.classList.add('opacity-50');
    } else {
        spinnerEl.classList.add('d-none');
        contenedorEl.classList.remove('opacity-50');
    }
}

function cambiarEstadoBotonProducto(cargando) {
    dom.btnGuardarProducto.disabled = cargando;
    dom.spinnerBtnProducto.classList.toggle('d-none', !cargando);
    dom.iconoBtnProducto.classList.toggle('d-none', cargando);
}

function cambiarEstadoBotonCategoria(cargando) {
    dom.btnGuardarCategoria.disabled = cargando;
    dom.spinnerBtnCategoria.classList.toggle('d-none', !cargando);
    dom.iconoBtnCategoria.classList.toggle('d-none', cargando);
}

function mostrarAlerta(mensaje, tipo = 'danger') {
    const iconos = {
        success: 'bi-check-circle-fill',
        danger: 'bi-exclamation-triangle-fill',
        warning: 'bi-exclamation-circle-fill',
        info: 'bi-info-circle-fill'
    };

    const alerta = document.createElement('div');
    alerta.className = `alert alert-${tipo} alert-dismissible fade show shadow-sm d-flex align-items-center mb-2`;
    alerta.setAttribute('role', 'alert');
    alerta.innerHTML = `
        <i class="bi ${iconos[tipo] || iconos.info} me-2 fs-5"></i>
        <div>${escaparHTML(mensaje)}</div>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Cerrar"></button>
    `;

    dom.alertContainer.prepend(alerta);

    setTimeout(() => {
        if (alerta.parentNode) {
            alerta.classList.remove('show');
            setTimeout(() => alerta.remove(), 200);
        }
    }, 6000);
}

function obtenerNombreCategoria(categoriaId) {
    if (!categoriaId) return 'Sin Categoría';
    const encontrada = estadoCategorias.find(c => c.id === Number(categoriaId));
    return encontrada ? encontrada.nombre : `Categoría #${categoriaId}`;
}

async function parsearRespuestaJSON(response) {
    const texto = await response.text();
    if (!texto) return {};
    try {
        return JSON.parse(texto);
    } catch (e) {
        return { mensaje: texto };
    }
}

function extraerMensajeError(data, status) {
    if (data && (data.mensaje || data.error)) {
        return `${data.mensaje || data.error} (HTTP ${status})`;
    }
    return `Error en la petición HTTP (Código ${status})`;
}

function escaparHTML(texto) {
    if (texto === null || texto === undefined) return '';
    return String(texto)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}
