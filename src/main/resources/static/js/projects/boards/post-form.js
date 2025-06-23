document.addEventListener('DOMContentLoaded', () => {
  const form      = document.getElementById('post-form');
  const editor    = document.getElementById('editor');
  const hidden    = document.getElementById('content-hidden');
  const btnSave   = document.getElementById('btn-save');
  const btnCancel = document.getElementById('btn-cancel');
  const pid    = PROJECT_ID;
  const bid    = BOARD_ID;
  const postId = POST_ID;

  editor.addEventListener('dragover', e => e.preventDefault());
    editor.addEventListener('drop', async e => {
      e.preventDefault();
      const file = e.dataTransfer.files[0];
      if (!file.type.startsWith('image/')) return;
      const reader = new FileReader();
      reader.onload = () => {
        const img = document.createElement('img');
        img.src = reader.result;
        img.style.maxWidth = '100%';
        editor.appendChild(img);
      };
      reader.readAsDataURL(file);
    });

  // 저장 버튼
   btnSave.addEventListener('click', async () => {
     // 1) editor의 내용을 hidden 필드로 복사
     hidden.value = editor.innerHTML;

     // 2) FormData 준비
     const fd = new FormData(form);
     // 만약 파일도 JSON과 함께 이중 전송해야 하면, 이전에 사용하던 fetch Multipart 방식으로 변경

     // 3) REST 호출
     const method = postId ? 'PUT' : 'POST';
     const url    = postId
       ? `/api/projects/${pid}/boards/${bid}/posts/${postId}`
       : `/api/projects/${pid}/boards/${bid}/posts`;

     const res = await fetch(url, { method, body: fd });
     if (!res.ok) return alert('저장 실패');
     // 목록 페이지로 복귀
     window.location.href = `/projects/${pid}/boards/${bid}/posts`;
   });

  // 취소 버튼
  btnCancel.addEventListener('click', () => {
    window.location.href = `/projects/${pid}/boards/${bid}/posts`;
  });
});
