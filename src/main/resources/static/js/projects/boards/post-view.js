document.addEventListener('DOMContentLoaded', () => {
  const editBtn   = document.getElementById('btn-edit');
  const deleteBtn = document.getElementById('btn-delete');
  const listBtn   = document.getElementById('btn-list');

  editBtn.addEventListener('click', () => {
    window.location.href = `/projects/${PROJECT_ID}/boards/${BOARD_ID}/posts/${POST_ID}/edit`;
  });

  deleteBtn.addEventListener('click', async () => {
    if (!confirm('정말 삭제하시겠습니까?')) return;
    const res = await fetch(
      `/api/projects/${PROJECT_ID}/boards/${BOARD_ID}/posts/${POST_ID}`,
      { method: 'DELETE' }
    );
    if (!res.ok) return alert('삭제에 실패했습니다.');
    window.location.href = `/projects/${PROJECT_ID}/boards/${BOARD_ID}/posts`;
  });

  listBtn.addEventListener('click', () => {
    window.location.href = `/projects/${PROJECT_ID}/boards/${BOARD_ID}/posts`;
  });
});
