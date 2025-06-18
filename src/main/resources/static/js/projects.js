document.getElementById('btn-new').addEventListener('click', () => {
  const name = prompt('새 프로젝트 이름을 입력하세요');
  if (!name) return;
  fetch('/api/projects', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({ name })
  })
  .then(_ => location.reload());
});
