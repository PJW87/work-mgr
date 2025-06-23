// static/js/projects.js

document.addEventListener('DOMContentLoaded', () => {
  // 1) 프로젝트 로드
  loadProjects();

  // 2) 모달 오픈/클로즈 버튼 바인딩
  document.getElementById('btn-new-project').addEventListener('click', () => openModal());
  document.getElementById('btn-cancel-modal').addEventListener('click', closeModal);

  // 3) 오버레이 클릭 시에도 모달 닫기
  document.getElementById('modal-overlay').addEventListener('click', (e) => {
    if (e.target.id === 'modal-overlay') closeModal();
  });

  // 4) 새 프로젝트 폼 제출
  document.getElementById('form-new-project')
    .addEventListener('submit', submitProject);
});

function openModal(project) {
  const form = document.getElementById('form-new-project');
   // 폼 초기화
   form.reset();
   form.id.value = ''; // hidden input

   if (project && project.id) {
     // 수정 모드: 값 채우기
     form.id.value = project.id;
     form.name.value = project.name || '';
     form.description.value = project.description || '';
     // project.startDate 는 "YYYY-MM-DD"
     // month input 은 "YYYY-MM"
     form.startDate.value = project.startDate
       ? project.startDate.slice(0,7)
       : '';
     document.querySelector('#modal h2').textContent = '프로젝트 수정';
     form.querySelector('button[type=submit]').textContent = '수정';
   } else {
     // 생성 모드
     document.querySelector('#modal h2').textContent = '새 프로젝트 생성';
     form.querySelector('button[type=submit]').textContent = '생성';
   }

   document.getElementById('modal-overlay')
     .classList.remove('hidden');
 }

async function closeModal() {
  document.getElementById('modal-overlay').classList.add('hidden');
}

async function submitProject(e) {
  e.preventDefault();
  const form = e.target;
  const id = form.id.value;
  const payload = {
    name:        form.name.value.trim(),
    description: form.description.value.trim(),
    // input[type=month] 값이 "YYYY-MM"
    // 서버에서는 LocalDate.parse("YYYY-MM-01") 로 처리
    startDate:   form.startDate.value + '-01'
  };

  const url    = id ? `/api/projects/${id}` : '/api/projects/create';
  const method = id ? 'PUT' : 'POST';

  try {
    const res = await fetch(url, {
      method,
      headers: {'Content-Type':'application/json'},
      body:    JSON.stringify(payload)
    });
    if (!res.ok) throw new Error(`${method} 실패 (${res.status})`);

    closeModal();
    loadProjects();
  } catch (err) {
    alert(err.message);
  }
}

async function loadProjects() {
  try {
    const res  = await fetch('/api/projects');
    if (!res.ok) throw new Error(`목록 로드 실패 (${res.status})`);
    const data = await res.json();

    const container = document.getElementById('project-list-container');
    container.innerHTML = '';

    data.forEach(proj => {
      const card = document.createElement('div');
      card.className = 'card';
      card.innerHTML = `
        <div class="card-header">${proj.name}</div>
        <div class="card-body">
          <p>${proj.description || ''}</p>
          <p>시작일: ${proj.startDate ? proj.startDate.slice(0,7) : '미정'}</p>
        </div>
        <div class="card-footer">
          <button class="btn btn-manage" data-id="${proj.id}">관리하기</button>
          <button class="btn btn-edit"   data-id="${proj.id}">수정</button>
          <button class="btn btn-delete" data-id="${proj.id}">삭제</button>
        </div>
      `;
      container.appendChild(card);
    });

    attachActionHandlers();
  } catch (err) {
    alert(err.message);
  }
}

function attachActionHandlers() {
  document.querySelectorAll('.btn-delete').forEach(btn => {
    btn.addEventListener('click', async e => {
      const id = e.target.dataset.id;
      if (!confirm('정말 삭제하시겠습니까?')) return;
      try {
        const res = await fetch(`/api/projects/${id}`, { method: 'DELETE' });
        if (!res.ok) throw new Error(`삭제 실패 (${res.status})`);
        loadProjects();
      } catch (err) {
        alert(err.message);
      }
    });
  });

  document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.addEventListener('click', async e => {
      const id = e.target.dataset.id;
      try {
        const res = await fetch(`/api/projects/${id}`);
        if (!res.ok) throw new Error(`조회 실패 (${res.status})`);
        const project = await res.json();
        openModal(project);
      } catch (err) {
        alert(err.message);
      }
    });
  });

  document.querySelectorAll('.btn-manage').forEach(btn => {
    btn.addEventListener('click', e => {
      const id = e.target.dataset.id;
      window.location.href = `/projects/${id}/boards`;
    });
  });
}
