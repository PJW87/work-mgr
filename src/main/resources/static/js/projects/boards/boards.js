// src/main/resources/static/js/projects/boards/boards.js
document.addEventListener('DOMContentLoaded', () => {
  const newBtn    = document.getElementById('btn-new-board');
  const cancelBtn = document.getElementById('btn-cancel-modal');
  const form      = document.getElementById('form-new-board');
  const overlay   = document.getElementById('modal-overlay');
  const title     = document.querySelector('.modal-title');

  newBtn.addEventListener('click', () => openModal());
  cancelBtn.addEventListener('click', () => closeModal());
  form.addEventListener('submit', submitBoard);

  attachDeleteHandlers();
  attachEditHandlers();

  document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeModal();
  });
});

let editMode = false;     // 새로 만들기 vs 수정 모드 구분
let editBoardId = null;   // 수정 중인 게시판 ID

function openModal(board = null) {
  const overlay = document.getElementById('modal-overlay');
  const title   = document.querySelector('.modal-title');
  const nameIn  = document.getElementById('board-name');
  const descIn  = document.getElementById('board-desc');
  const submitBtn = document.querySelector('#form-new-board button[type="submit"]');
  if (board) {
    // 수정 모드
    editMode     = true;
    editBoardId  = board.id;
    title.textContent = '게시판 수정';
    submitBtn.textContent = '수정';
    nameIn.value = board.name;
    descIn.value = board.description || '';
  } else {
    // 새로 만들기 모드
    editMode     = false;
    editBoardId  = null;
    title.textContent = '새 게시판 생성';
    submitBtn.textContent = '생성';
    nameIn.value = '';
    descIn.value = '';
  }

  overlay.classList.add('active');
  setTimeout(() => nameIn.focus(), 50);
}

function closeModal() {
  document.getElementById('modal-overlay').classList.remove('active');
}
async function submitBoard(e) {
  e.preventDefault();
  const f = e.target;
  const data = {
    projectId:  f.projectId.value,
    name:       f.name.value.trim(),
    description:f.description.value.trim()
  };

  const url    = editMode
    ? `/api/projects/${data.projectId}/boards/${editBoardId}`
    : `/api/projects/${data.projectId}/boards`;
  const method = editMode ? 'PUT' : 'POST';

  const res = await fetch(url, {
    method,
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify(data)
  });

  if (!res.ok) {
    alert(`${editMode ? '수정' : '생성'} 실패 (${res.status})`);
    return;
  }

  closeModal();
  location.reload();
}

function attachDeleteHandlers() {
  document.querySelectorAll('.btn-delete').forEach(btn => {
    btn.addEventListener('click', async e => {
      if (!confirm('정말 삭제하시겠습니까?')) return;
      const bid = e.target.dataset.id;
      await fetch(`/api/projects/${PROJECT_ID}/boards/${bid}`, { method: 'DELETE' });
      location.reload();
    });
  });
}

function attachEditHandlers() {
  document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.addEventListener('click', async e => {
      const bid = e.target.dataset.id;
      const res = await fetch(`/api/projects/${PROJECT_ID}/boards/${bid}`);
      if (!res.ok) return alert('게시판 조회 실패');
      const board = await res.json();
      openModal(board);
    });
  });
}