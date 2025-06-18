// 페이지 로드 시 전체 이슈 로드
document.addEventListener('DOMContentLoaded', loadIssues);

// 검색 버튼 핸들러
document.getElementById('btn-search-issue').addEventListener('click', () => {
  const keyword = document.getElementById('issue-search').value.trim();
  loadIssues(keyword);
});

// 이슈 등록 버튼 핸들러
document.getElementById('btn-add-issue').addEventListener('click', () => {
  const title = prompt('이슈 제목을 입력하세요');
  const desc  = prompt('이슈 내용(설명)을 입력하세요');
  if (!title) return;
  fetch('/api/issues', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({ title, description: desc })
  })
  .then(res => res.json())
  .then(_ => loadIssues());
});

function loadIssues(keyword = '') {
  const url = keyword
    ? `/api/issues?search=${encodeURIComponent(keyword)}`
    : '/api/issues';
  fetch(url)
    .then(res => res.json())
    .then(data => {
      const ul = document.getElementById('issue-list');
      ul.innerHTML = '';
      if (data.length === 0) {
        ul.innerHTML = '<li>등록된 이슈가 없습니다.</li>';
        return;
      }
      data.forEach(item => {
        const li = document.createElement('li');
        li.innerHTML = `
          <strong>${item.title}</strong>
          <p>${item.description}</p>
          <button data-id="${item.id}" class="btn-edit-issue">수정</button>
          <button data-id="${item.id}" class="btn-delete-issue">삭제</button>
        `;
        ul.appendChild(li);
      });
      attachIssueHandlers();
    });
}

function attachIssueHandlers() {
  document.querySelectorAll('.btn-delete-issue').forEach(btn => {
    btn.addEventListener('click', e => {
      const id = e.target.dataset.id;
      fetch(`/api/issues/${id}`, { method: 'DELETE' })
        .then(_ => loadIssues());
    });
  });
  document.querySelectorAll('.btn-edit-issue').forEach(btn => {
    btn.addEventListener('click', e => {
      const id = e.target.dataset.id;
      const newTitle = prompt('새 이슈 제목', '');
      const newDesc  = prompt('새 이슈 내용', '');
      if (!newTitle) return;
      fetch(`/api/issues/${id}`, {
        method: 'PUT',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify({ title: newTitle, description: newDesc })
      })
      .then(_ => loadIssues());
    });
  });
}
