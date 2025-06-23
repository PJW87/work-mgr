// src/main/resources/static/js/projects/boards/posts.js
document.addEventListener('DOMContentLoaded', ()=>{
  const pid = PROJECT_ID;
  const bid = BOARD_ID;
  let page=0, size=10;

  // UI 엘리먼트
  const overlay = document.getElementById('modal-overlay');
  const form    = document.getElementById('form-post');
  const editor  = document.getElementById('editor');
  const tblBody = document.getElementById('post-list');
  const pagDiv  = document.getElementById('pagination');

  form.onsubmit = submitPost;
  editor.addEventListener('drop', handleDrop);

  loadPosts();
    document.getElementById('btn-new-post').onclick = () => {
       window.location.href = `/projects/${pid}/boards/${bid}/posts/new`;
     };
  // --- 함수들 ---
  async function loadPosts(){
    const res = await fetch(`/api/projects/${pid}/boards/${bid}/posts?page=${page}&size=${size}`);
    const jr  = await res.json();
    renderTable(jr.data);
    renderPagination(jr.total, jr.page, jr.size);
  }

function renderTable(data) {
  tblBody.innerHTML = data.map((p, idx) => `
    <tr>
      <td><input type="checkbox" class="chk-item" data-id="${p.id}"/></td>
      <td>${idx + 1 + page*size}</td>
      <td>
        <a href="/projects/${pid}/boards/${bid}/posts/${p.id}" class="post-link">
          ${p.title}
        </a>
      </td>
      <td>${p.author}</td>
      <td>${translateStatus(p.status)}</td>
      <td>${p.createdAt.slice(0,10)}</td>
    </tr>
  `).join('');
  attachRowHandlers();
  bindBatchDelete();
  bindSelectAll();
}
// 상태 한글화
function translateStatus(s) {
  return {
    OPEN: '열림',
    IN_PROGRESS: '진행 중',
    RESOLVED: '해결됨',
    CLOSED: '종료'
  }[s] || s;
}
// --- 2) 전체선택/해제 ---
function bindSelectAll() {
  document.getElementById('chk-all').onchange = e => {
    document.querySelectorAll('.chk-item')
      .forEach(cb => cb.checked = e.target.checked);
  };
}
// --- 3) 일괄 삭제 바인딩 ---
function bindBatchDelete() {
  document.getElementById('btn-batch-delete').onclick = async () => {
    const ids = Array.from(document.querySelectorAll('.chk-item:checked'))
      .map(cb => cb.dataset.id);
    if (!ids.length) return alert('삭제할 글을 선택하세요.');
    if (!confirm(`${ids.length}개 글을 삭제하시겠습니까?`)) return;
    // 병렬 요청
    await Promise.all(ids.map(id =>
      fetch(`/api/projects/${pid}/boards/${bid}/posts/${id}`, { method:'DELETE' })
    ));
    loadPosts();
  };
}

  function renderPagination(total, pg, sz){
    const pages = Math.ceil(total/sz);
    pagDiv.innerHTML = Array.from({length:pages})
      .map((_,i)=>`<button class="pg-btn"${i===pg?' disabled':''} data-page="${i}">${i+1}</button>`)
      .join(' ');
    pagDiv.querySelectorAll('.pg-btn')
      .forEach(b=>b.onclick=()=>{ page=+b.dataset.page; loadPosts(); });
  }

  function attachRowHandlers(){
    tblBody.querySelectorAll('.btn-edit').forEach(b=>{
      b.onclick = async ()=> {
        const id = b.dataset.id;
        const r  = await fetch(`/api/projects/${pid}/boards/${bid}/posts/${id}`);
        const j  = await r.json();
        openModal(j.post, j.attachments);
      };
    });
    tblBody.querySelectorAll('.btn-delete').forEach(b=>{
      b.onclick = async ()=>{
        if(!confirm('정말 삭제?')) return;
        await fetch(`/api/projects/${pid}/boards/${bid}/posts/${b.dataset.id}`, {method:'DELETE'});
        loadPosts();
      };
    });
  }

  function openModal(post={}, atts=[]){
    form.postId.value   = post.id||'';
    form.title.value    = post.title||'';
    form.author.value   = post.author||'';
    form.status.value   = post.status||'OPEN';
    editor.innerHTML     = post.content||'';
    overlay.classList.remove('hidden');
  }
  function closeModal(){
    overlay.classList.add('hidden');
    form.reset();
    editor.innerHTML='';
  }

  async function submitPost(e){
    e.preventDefault();
    const fd = new FormData();
    const p  = {
      title:  form.title.value.trim(),
      author: form.author.value.trim(),
      status: form.status.value,
      content: editor.innerHTML
    };
    fd.append('post', new Blob([JSON.stringify(p)],{type:'application/json'}));
    for(let f of form.files.files) fd.append('files', f);
    const method = form.postId.value ? 'PUT':'POST';
    const url    = form.postId.value
      ? `/api/projects/${pid}/boards/${bid}/posts/${form.postId.value}`
      : `/api/projects/${pid}/boards/${bid}/posts`;
    const res = await fetch(url, {method, body:fd});
    if(!res.ok) return alert('저장 실패');
    closeModal();
    loadPosts();
  }

  function handleDrop(e){
    e.preventDefault();
    const file = e.dataTransfer.files[0];
    if(!file.type.startsWith('image/')) return;
    const reader = new FileReader();
    reader.onload = ()=> {
      const img = document.createElement('img');
      img.src = reader.result;
      img.style.maxWidth='100%';
      editor.appendChild(img);
    };
    reader.readAsDataURL(file);
  }
});
