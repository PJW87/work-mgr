document.addEventListener('DOMContentLoaded', () => {
  const form      = document.getElementById('issue-form');
  const btnSave   = document.getElementById('btn-save');
  const btnCancel = document.getElementById('btn-cancel');
  // 서버에 보낼 때 이 필드를 기준으로 POST/PUT 분기
  const issueId = ISSUE_ID;

  btnSave.addEventListener('click', async () => {
    // 입력값 모으기
    const data = {
      id:      issueId || null,
      title: form.title.value.trim(),
      author: form.author.value.trim(),
      status: form.status.value,
      content: editor.innerHTML
    };

    const method = data.id ? 'PUT' : 'POST';
    const url    = data.id
      ? `/api/issues/${data.id}`
      : '/api/issues';

    const res = await fetch(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(data)
    });

    if (!res.ok) {
      const txt = await res.text();
      console.error('이슈 저장 실패:', txt);
      return alert('이슈 저장 중 오류가 발생했습니다.');
    }
    // 저장 성공하면 리스트로
    window.location.href = '/issues';
  });
    editor.addEventListener('dragover', e => e.preventDefault());

    editor.addEventListener('drop', async e => {
      e.preventDefault();
      const file = e.dataTransfer.files[0];
      if (!file.type.startsWith('image/')) return;

      try {
        // 서버에 이미지 업로드
        const formData = new FormData();
        formData.append('file', file);

        // 이미지 업로드 API 호출 (서버에 구현 필요)
        const res = await fetch(`/api/issues/images`, {
          method: 'POST',
          body: formData
        });

        if (!res.ok) {
          alert('이미지 업로드 실패');
          return;
        }

        const data = await res.json();
        // 서버가 반환한 이미지 URL (예: { url: "/uploads/abc.jpg" })
        const imageUrl = data.url;

        // 본문에 이미지 삽입
        const img = document.createElement('img');
        img.src = imageUrl;
        img.style.maxWidth = '100%';
        editor.appendChild(img);

      } catch (err) {
        console.error(err);
        alert('이미지 업로드 중 오류가 발생했습니다.');
      }
    });

        interact('.editor img')
          .resizable({
            edges: { left: true, right: true, bottom: true, top: true },
            preserveAspectRatio: true,
          })
          .on('resizemove', function (event) {
            const img = event.target;
            // 새로운 크기 적용
            img.style.width  = event.rect.width + 'px';
            img.style.height = event.rect.height + 'px';
          });

  const observer = new MutationObserver(mutations => {
      for (const mut of mutations) {
        // removedNodes 에 이미지가 있는지 찾아서 삭제 API 콜
       mut.removedNodes.forEach(node => {
         if (node.nodeName === 'IMG') {
           // fullSrc 예: "http://localhost:8080/uploads/abc.jpg"
           const fullSrc = node.src;

           // 1) URL API 로 부터 path ("/uploads/abc.jpg") 만 추출
           const urlObj = new URL(fullSrc, window.location.origin);
           const uploadPath = urlObj.pathname; // "/uploads/abc.jpg"

           // 2) 서버에 DELETE 요청
           fetch(
             `/api/issues/images?path=${encodeURIComponent(uploadPath)}`,
             { method: 'DELETE' }
           ).catch(console.error);
         }
       });
      }
    });

    observer.observe(editor, {
      childList: true,
      subtree: true
    });

  btnCancel.addEventListener('click', () => {
    window.location.href = '/issues';
  });
});
