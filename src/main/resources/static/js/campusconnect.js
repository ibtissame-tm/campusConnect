/* CampusConnect — comportements UI (v1)
   Emplacement : src/main/resources/static/js/campusconnect.js
   Aucune dépendance. Les formulaires restent de vrais <form> Spring MVC :
   ce script ne fait qu'améliorer l'expérience (menu mobile, commentaires, modale, toast). */
(function () {
  'use strict';
  var $ = function (s, r) { return (r || document).querySelector(s); };
  var $$ = function (s, r) { return Array.prototype.slice.call((r || document).querySelectorAll(s)); };
  var PREVIEW = document.body.hasAttribute('data-preview'); // vrai uniquement dans l'aperçu statique

  /* --- Toast --- */
  var toastEl = $('#toast'), toastTimer;
  function toast(msg) {
    if (!toastEl) return;
    toastEl.textContent = msg; toastEl.classList.add('show');
    clearTimeout(toastTimer); toastTimer = setTimeout(function () { toastEl.classList.remove('show'); }, 2400);
  }
  window.ccToast = toast;

  /* --- Menu mobile --- */
  var sidebar = $('#sidebar'), overlay = $('#overlay'), toggle = $('#menuToggle');
  function setMenu(open) {
    if (!sidebar) return;
    sidebar.classList.toggle('open', open);
    if (overlay) overlay.classList.toggle('show', open);
  }
  if (toggle) toggle.addEventListener('click', function () { setMenu(!sidebar.classList.contains('open')); });
  if (overlay) overlay.addEventListener('click', function () { setMenu(false); });
  document.addEventListener('keydown', function (e) { if (e.key === 'Escape') { setMenu(false); closeModal(); } });

  /* --- Afficher / masquer les commentaires --- */
  $$('[data-toggle-comments]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var panel = document.getElementById(btn.getAttribute('data-toggle-comments'));
      if (!panel) return;
      var open = panel.classList.toggle('open');
      btn.setAttribute('aria-expanded', open);
      if (open) { var i = $('input[name="contenu"]', panel); if (i) i.focus({ preventScroll: true }); }
    });
  });

  /* --- Like : animation immédiate ; l'envoi reste un POST Spring MVC --- */
  $$('form[data-like]').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      var btn = $('.act', form);
      if (PREVIEW) {
        e.preventDefault();
        var liked = btn.classList.toggle('liked');
        var n = $('.count', btn); n.textContent = parseInt(n.textContent, 10) + (liked ? 1 : -1);
      }
    });
  });

  /* --- Follow / Unfollow --- */
  $$('form[data-follow]').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!PREVIEW) return;
      e.preventDefault();
      var btn = $('button', form), on = btn.classList.toggle('btn-following');
      btn.classList.toggle('btn-soft', !on);
      btn.textContent = on ? 'Suivi' : 'Suivre';
      toast(on ? 'Vous suivez ce club' : 'Vous ne suivez plus ce club');
    });
  });

  /* --- Signalement : modale de confirmation --- */
  var modal = $('#reportModal'), reportForm = modal && $('form', modal);
  function closeModal() { if (modal) modal.classList.remove('open'); }
  $$('[data-report]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      if (!modal) return;
      reportForm.setAttribute('action', btn.getAttribute('data-report'));
      modal.classList.add('open');
    });
  });
  $$('[data-close-modal]').forEach(function (b) { b.addEventListener('click', closeModal); });
  if (modal) modal.addEventListener('click', function (e) { if (e.target === modal) closeModal(); });
  if (reportForm) reportForm.addEventListener('submit', function (e) {
    if (PREVIEW) { e.preventDefault(); closeModal(); toast('Signalement envoyé. Le contenu est masqué en attendant l’administration.'); }
  });

  /* --- Commentaire (aperçu seulement) --- */
  $$('form[data-comment]').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!PREVIEW) return;
      e.preventDefault();
      var input = $('input[name="contenu"]', form);
      if (!input.value.trim()) return;
      var wrap = document.createElement('div'); wrap.className = 'comment';
      wrap.innerHTML = '<span class="avatar avatar-sm"></span><div><b></b><p></p></div>';
      $('.avatar', wrap).textContent = 'AS'; $('b', wrap).textContent = 'Aya Smali'; $('p', wrap).textContent = input.value;
      form.parentNode.insertBefore(wrap, form); input.value = '';
    });
  });

  /* --- Demande d'adhésion (aperçu seulement) --- */
  $$('form[data-join]').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      if (!PREVIEW) return;
      e.preventDefault();
      var b = $('button', form); b.textContent = 'Demande envoyée'; b.disabled = true; b.classList.add('btn-soft');
      toast('Demande d’adhésion envoyée (EN_ATTENTE)');
    });
  });

  /* --- Modales génériques : <button data-open-modal="id"> ouvre #id --- */
  $$('[data-open-modal]').forEach(function (b) {
    b.addEventListener('click', function () {
      var m = document.getElementById(b.getAttribute('data-open-modal'));
      if (m) m.classList.add('open');
    });
  });
  $$('.modal').forEach(function (m) {
    $$('[data-close-modal]', m).forEach(function (b) { b.addEventListener('click', function () { m.classList.remove('open'); }); });
    m.addEventListener('click', function (e) { if (e.target === m) m.classList.remove('open'); });
  });

  /* --- Onglets : <button data-tab="x" data-tab-group="g"> + <div class="tab-panel" data-panel="x" data-tab-group="g"> --- */
  $$('[data-tab]').forEach(function (btn) {
    btn.addEventListener('click', function () {
      var g = btn.getAttribute('data-tab-group'), name = btn.getAttribute('data-tab');
      $$('[data-tab][data-tab-group="' + g + '"]').forEach(function (b) { b.classList.toggle('active', b === btn); });
      $$('.tab-panel[data-tab-group="' + g + '"]').forEach(function (p) { p.classList.toggle('active', p.getAttribute('data-panel') === name); });
    });
  });


  /* --- Mot de passe : jauge de robustesse + confirmation --- */
  var pw = $('input[data-strength]'), bar = $('.strength i'), confirm = $('input[data-confirm]');
  if (pw && bar) pw.addEventListener('input', function () {
    var v = pw.value, s = 0;
    if (v.length >= 8) s++; if (/[A-Z]/.test(v)) s++; if (/[0-9]/.test(v)) s++; if (/[^A-Za-z0-9]/.test(v)) s++;
    bar.style.width = (s * 25) + '%';
    bar.style.background = s <= 1 ? 'var(--danger)' : s == 2 ? 'var(--warning)' : s == 3 ? 'var(--blue-600)' : 'var(--success)';
  });
  if (pw && confirm) {
    var check = function () { confirm.setCustomValidity(confirm.value && confirm.value !== pw.value ? 'Les mots de passe ne correspondent pas' : ''); };
    pw.addEventListener('input', check); confirm.addEventListener('input', check);
  }

  /* --- Aperçu : toute soumission est simulée (aucun serveur) --- */
  if (PREVIEW) document.addEventListener('submit', function (e) {
    if (!e.defaultPrevented) { e.preventDefault(); toast('Aperçu : action simulée'); }
  });

  /* --- Liens inertes dans l'aperçu --- */
  if (PREVIEW) $$('a[href^="#"], a[data-demo]').forEach(function (a) { a.addEventListener('click', function (e) { e.preventDefault(); }); });
})();
