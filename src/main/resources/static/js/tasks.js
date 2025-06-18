// 페이지 로드 시 업무 목록 불러오기
document.addEventListener('DOMContentLoaded', loadTasks);

// 업무 등록 버튼 핸들러
document.getElementById('btn-add-task').addEventListener('click', () => {
  const title  = prompt('업무 제목을 입력하세요');
  const status = prompt('상태를 입력하세요 (PENDING, IN_PROGRESS, COMPLETED, PLANNED)');
  if (!title || !status) return;

  fetch('/api/tasks', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({ title, status })
  })
  .then(res => res.json())
  .then(_ => loadTasks());
});

function loadTasks() {
  fetch('/api/tasks')
    .then(res => res.json())
    .then(data => {
      const ul = document.getElementById('task-list');
      ul.innerHTML = '';
      if (data.length === 0) {
        ul.innerHTML = '<li>등록된 업무가 없습니다.</li>';
        return;
      }
      data.forEach(item => {
        const li = document.createElement('li');
        li.innerHTML = `
          <span class="status">[${item.status}]</span>
          ${item.title}
          <button data-id="${item.id}" class="btn-edit">수정</button>
          <button data-id="${item.id}" class="btn-delete">삭제</button>
        `;
        ul.appendChild(li);
      });
      attachTaskHandlers();
    });
}

function attachTaskHandlers() {
  document.querySelectorAll('.btn-delete').forEach(btn => {
    btn.addEventListener('click', e => {
      const id = e.target.dataset.id;
      fetch(`/api/tasks/${id}`, { method: 'DELETE' })
        .then(_ => loadTasks());
    });
  });

  document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.addEventListener('click', e => {
      const id = e.target.dataset.id;
      const newTitle  = prompt('새 제목', '');
      const newStatus = prompt('새 상태 (PENDING, IN_PROGRESS, COMPLETED, PLANNED)', '');
      if (!newTitle || !newStatus) return;
      fetch(`/api/tasks/${id}`, {
        method: 'PUT',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({ title: newTitle, status: newStatus })
      })
      .then(_ => loadTasks());
    });
  });
}
