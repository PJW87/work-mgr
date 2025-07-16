document.addEventListener('DOMContentLoaded', () => {
  initSorting();
  loadLinks();
});

let searchTerm = '';
// ① 검색창 이벤트 바인딩
document.getElementById('link-search')
  .addEventListener('input', e => {
    searchTerm = e.target.value.trim().toLowerCase();
    currentPage = 1;
    renderLinks();
  });
// 검색 클리어 버튼
document.getElementById('btn-clear-search')
  .addEventListener('click', () => {
    document.getElementById('link-search').value = '';
    searchTerm = '';
    currentPage = 1;
    renderLinks();
  });

// 페이징 설정
let currentPage = 1;
const pageSize = 5;

// 정렬 상태
let sortField = null;
let sortAsc = true;

function initSorting() {
  document.querySelectorAll('#link-section th[data-field]').forEach(th => {
    th.addEventListener('click', () => {
      const field = th.dataset.field;
      if (sortField === field) {
        sortAsc = !sortAsc;
      } else {
        sortField = field;
        sortAsc = true;
      }
      // 헤더 UI 업데이트
      document.querySelectorAll('.sort-indicator').forEach(span => span.textContent = '');
      th.querySelector('.sort-indicator').textContent = sortAsc ? '▲' : '▼';
      renderLinks();  // 이미 받아온 데이터로 재렌더
    });
  });
}

// 데이터 저장
let _linksData = [];

function loadLinks() {
  fetch('/api/links')
    .then(res => res.json())
    .then(data => {
      _linksData = data;
      currentPage = 1;
      renderLinks();
    });
}

function renderLinks() {
  let data = [..._linksData];

  // 2) 검색 필터 적용
  if (searchTerm) {
    data = data.filter(item =>
      (item.title || '').toLowerCase().includes(searchTerm) ||
      (item.url   || '').toLowerCase().includes(searchTerm)
    );
  }
  // 정렬 적용
  if (sortField) {
    data.sort((a, b) => {
      const va = (a[sortField] || '').toString().toLowerCase();
      const vb = (b[sortField] || '').toString().toLowerCase();
      return (va > vb ? 1 : va < vb ? -1 : 0) * (sortAsc ? 1 : -1);
    });
  }

  // 페이징 적용
  const totalPages = Math.ceil(data.length / pageSize) || 1;
  const start = (currentPage - 1) * pageSize;
  const pageData = data.slice(start, start + pageSize);

  // 테이블에 렌더
  const tbody = document.getElementById('link-list');
  tbody.innerHTML = '';
  pageData.forEach(item => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td title="${item.title}">${item.title}</td>
      <td><a href="${item.url}" target="_blank" data-id="${item.id}" class="link-item"> ${item.url}</a></td>
      <td>${item.description || ''}</td>
      <td>
        <button class="btn-edit btn btn-secondary" data-id="${item.id}">수정</button>
        <button class="btn-delete btn btn-danger" data-id="${item.id}">삭제</button>
      </td>`;
    tbody.appendChild(tr);
  });
  attachHandlers();
  renderPager(totalPages);
}

function renderPager(totalPages) {
  const pager = document.getElementById('link-pager');
  pager.innerHTML = '';
  for (let i = 1; i <= totalPages; i++) {
    const btn = document.createElement('button');
    btn.textContent = i;
    btn.className = 'pg-btn' + (i === currentPage ? ' active' : '');
    btn.addEventListener('click', () => {
      currentPage = i;
      renderLinks();
    });
    pager.appendChild(btn);
  }
}

// 모달 관리
const modal = document.getElementById('modal-link-form');
function openModal(mode, data) {
  document.getElementById('form-title').textContent = mode === 'edit' ? '링크 수정' : '새 링크';
  const form = document.getElementById('link-form');
  form.id.value          = data?.id || '';
  form.title.value       = data?.title || '';
  form.url.value         = data?.url || '';
  form.description.value = data?.description || '';
  modal.classList.remove('hidden');
}
function closeModal() {
  modal.classList.add('hidden');
}

// 이벤트 바인딩
document.getElementById('btn-add-link').addEventListener('click', () => openModal('add'));
document.getElementById('cancel-link').addEventListener('click', closeModal);
document.getElementById('save-link').addEventListener('click', () => {
  const form = document.getElementById('link-form');
  const payload = {
    title: form.title.value,
    url: form.url.value,
    description: form.description.value
  };
  const id = form.id.value;
  const method = id ? 'PUT' : 'POST';
  const url = id ? `/api/links/${id}` : '/api/links';

  fetch(url, {
    method,
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify(payload)
  })
  .then(res => {
    if (!res.ok) throw new Error('저장 실패');
    closeModal();
    loadLinks();
  });
});

function attachHandlers() {

  // 클릭 카운트 증가 (mousedown 단계에서 호출)
  document.querySelectorAll('a.link-item').forEach(a => {
    a.addEventListener('click', e => {
      const id = a.dataset.id;
      fetch(`/api/links/${id}/click`, { method: 'POST' })
        .catch(console.error);
      // 또는 navigator.sendBeacon(`/api/links/${id}/click`);
    });
  });

  document.querySelectorAll('.btn-delete').forEach(btn => {
    btn.onclick = e => {
      if (!confirm('삭제하시겠습니까?')) return;
      fetch(`/api/links/${e.target.dataset.id}`, { method: 'DELETE' })
        .then(_ => loadLinks());
    };
  });
  document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.onclick = e => {
      fetch(`/api/links/${e.target.dataset.id}`)
        .then(res => res.json())
        .then(data => openModal('edit', data));
    };
  });
}
