document.addEventListener('DOMContentLoaded', () => {
  const editBtn   = document.getElementById('btn-edit');
  const deleteBtn = document.getElementById('btn-delete');
  const listBtn   = document.getElementById('btn-list');
  const issueId = ISSUE_ID;
  const statusEl = document.getElementById('status');
    if (statusEl) {
        const raw = statusEl.textContent.trim();
        statusEl.textContent = translateStatus(raw);
      }

  editBtn.addEventListener('click', () => {
    window.location.href = `/issues/${issueId}/edit`;
  });

// 상태 한글화
  function translateStatus(s) {
    return {
      OPEN: '열림',
      IN_PROGRESS: '진행 중',
      RESOLVED: '해결됨',
      CLOSED: '종료'
    }[s] || s;
  }
  deleteBtn.addEventListener('click', async () => {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    const res = await fetch(
      `/api/issues/${issueId}`,
      { method: 'DELETE' }
    );
    if (!res.ok) return alert('삭제에 실패했습니다.');
    window.location.href = `/issues`;
  });

  listBtn.addEventListener('click', () => {
    window.location.href = `/issues`;
  });
});
