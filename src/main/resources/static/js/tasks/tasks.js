document.addEventListener('DOMContentLoaded', () => {
  const listEl       = document.getElementById('task-list');
  const btnNew       = document.getElementById('btn-new-task');
  const formModal    = document.getElementById('modal-task-form');
  const viewModal    = document.getElementById('modal-task-view');
  const pagerEl     = document.getElementById('task-pager');
  const btnSearch   = document.getElementById('task-btn-search');

  // 검색바 엘리먼트
  const sType       = document.getElementById('task-search-type');
  const sKey        = document.getElementById('task-search-keyword');
  const sProj       = document.getElementById('task-search-project');
  const sFrom       = document.getElementById('task-search-from');
  const sTo         = document.getElementById('task-search-to');

  // form elements
  const form         = document.getElementById('task-form');
  const projSel      = form.projectId;
  const titleInp     = form.title;
  const descInp      = form.description;
  const asgInp       = form.assignee;
  const prioSel      = form.priority;
  const dueInp       = form.dueDate;
  const statusSel    = form.status;
  document.getElementById('save-task').onclick   = saveTask;
  document.getElementById('cancel-task').onclick = () => formModal.classList.add('hidden');

  // view elements
  const viewTitle    = document.getElementById('view-title-val');
  const viewDesc     = document.getElementById('view-desc-val');
  const viewAsg      = document.getElementById('view-assignee');
  const viewPrio     = document.getElementById('view-priority');
  const viewDue      = document.getElementById('view-dueDate');
  const viewStatus   = document.getElementById('view-status');
  const viewCreated  = document.getElementById('view-created');
  const viewUpdated  = document.getElementById('view-updated');
  document.getElementById('btn-close-view').onclick = () => viewModal.classList.add('hidden');
  document.getElementById('btn-edit-task').onclick  = () => { viewModal.classList.add('hidden');  // ← 상세보기 모달 숨김
                                                              openEdit(currentViewId);};
  document.getElementById('btn-delete-task').onclick= deleteTask;
//  fetch('/api/projects')
//      .then(r => r.json())
//      .then(list => {
//        list.forEach(p => {
//          const opt = document.createElement('option');
//          opt.value = p.id;
//          opt.textContent = p.name;
//          projSel.appendChild(opt);
//        });
//      });
  // 프로젝트 리스트 채우기 (검색바 + 등록폼 둘 다)
  fetch('/api/projects')
    .then(r => r.json())
    .then(list => {
      list.forEach(p => {
        const opt1 = document.createElement('option');
        opt1.value = p.id; opt1.textContent = p.name;
        sProj.appendChild(opt1);
        // 기존 폼용 셀렉트도 채워줌
        const opt2 = document.createElement('option');
        opt2.value = p.id; opt2.textContent = p.name;
        projSel.appendChild(opt2);
      });
    });
  // 페이지·사이즈 전역 변수
  let page = 0, size = 20;
  let currentViewId = null;

  btnNew.onclick = () => openNew();
  btnSearch.onclick = () => {
      // 1) 기간이 하나만 있으면 막기
      if ((sFrom.value && !sTo.value) || (!sFrom.value && sTo.value)) {
        return alert('기간을 시작일과 종료일 모두 입력해주세요.');
      }
      // 2) 시작 > 종료 방지
      if (sFrom.value && sTo.value && sTo.value < sFrom.value) {
        return alert('종료일은 시작일보다 이후여야 합니다.');
      }
      page = 0;
      loadTasks();
    };

  loadTasks();

  // 한글 매핑 함수들
  function mapPriority(p) {
    return ({
      CRITICAL: '긴급',
      HIGH:     '높음',
      MEDIUM:   '보통',
      LOW:      '낮음'
    })[p] || '-';
  }
  function mapStatus(s) {
    return ({
      OPEN:        '열림',
      IN_PROGRESS: '진행 중',
      IN_REVIEW:   '검토 중',
      DONE:        '완료',
      CANCELLED:   '취소'
    })[s] || '-';
  }



//  async function loadTasks() {
//    // 1) API 호출
//    const res = await fetch(`/api/tasks?page=${page}&size=${size}`);
//    const { data: arr, total, page: curPage, size: curSize } = await res.json();
//
//    // 3) 테이블 렌더링
//    listEl.innerHTML = arr.map(t => `
//      <tr data-id="${t.id}">
//        <td class="prio-${t.priority}">${mapPriority(t.priority)}</td>
//        <td class="link" title="${t.title}">${ t.projectName ? `<em>[${t.projectName}]</em> ` : '' } ${t.title}</td>
//            <td title="${t.assignee || '-'}">
//              ${t.assignee || '-'}
//            </td>
//
//        <td>${t.dueDate
//              ? t.dueDate.slice(0,16).replace('T',' ')
//              : '-'}</td>
//         <td>
//         <span class="status-badge status-${t.status}">${mapStatus(t.status)}</span>
//         </td>
//        <td>
//          <button class="btn btn-secondary btn-sm edit">수정</button>
//          <button class="btn btn-danger btn-sm del">삭제</button>
//        </td>
//      </tr>
//    `).join('');
//    attachRowHandlers();
//
//    // 4) 페이징 버튼 렌더링
//    const totalPages = Math.ceil(total / curSize);
//    let pagerHtml = '';
//    for (let i = 0; i < totalPages; i++) {
//      pagerHtml += `<button class="pg-btn"${i===curPage?' disabled':''} data-page="${i}">${i+1}</button>`;
//    }
//    document.getElementById('task-pager').innerHTML = pagerHtml;
//
//    // 5) 페이징 버튼 이벤트 바인딩
//    document.querySelectorAll('#task-pager .pg-btn').forEach(btn => {
//      btn.onclick = () => {
//        page = +btn.dataset.page;
//        loadTasks();
//      };
//    });
//  }
async function loadTasks() {
    const params = new URLSearchParams();
    params.set('type',    sType.value);
    params.set('keyword', sKey.value.trim());
    if (sProj.value)    params.set('projectId', sProj.value);
    if (sFrom.value && sTo.value) {
      params.set('from', sFrom.value);
      params.set('to',   sTo.value);
    }
    params.set('page', page);
    params.set('size', size);

    const res = await fetch(`/api/tasks?${params}`);
    if (!res.ok) {
      console.error(await res.text());
      return;
    }
    const { data: arr, total, page: curPage, size: curSize } = await res.json();

    // 기존 렌더링 코드 재활용
    listEl.innerHTML = arr.map(t => `
      <tr data-id="${t.id}">
        <td class="prio-${t.priority}">${mapPriority(t.priority)}</td>
        <td class="link" title="${t.title}">
          ${t.projectName?`<em>[${t.projectName}]</em> `:''}${t.title}
        </td>
        <td title="${t.assignee||'-'}">${t.assignee||'-'}</td>
        <td>${t.dueDate
              ? t.dueDate.slice(0,16).replace('T',' ')
              : '-'}</td>
        <td>
          <span class="status-badge status-${t.status}">
            ${mapStatus(t.status)}
          </span>
        </td>
        <td>
          <button class="btn btn-secondary btn-sm edit">수정</button>
          <button class="btn btn-danger btn-sm del">삭제</button>
        </td>
      </tr>
    `).join('');
    attachRowHandlers();

    // 페이징
    const pages = Math.ceil(total/curSize);
    pagerEl.innerHTML = '';
    for (let i=0; i<pages; i++) {
      const b = document.createElement('button');
      b.textContent = i+1;
      b.className   = 'pg-btn';
      b.disabled    = (i===curPage);
      b.onclick     = ()=>{ page=i; loadTasks(); };
      pagerEl.append(b);
    }
  }

  function attachRowHandlers() {
    listEl.querySelectorAll('tr').forEach(row => {
      const id = row.dataset.id;
      row.querySelector('.link').onclick = () => openView(id);
      row.querySelector('.edit').onclick = () => openEdit(id);
      row.querySelector('.del').onclick  = () => {
        if (!confirm('정말 삭제하시겠습니까?')) return;
        fetch(`/api/tasks/${id}`, { method:'DELETE' })
          .then(_=>loadTasks());
      };
    });
  }

  function openNew() {
    form.reset();
    document.getElementById('form-title').textContent = '새 업무';
    form.id.value = '';
    projSel.value = '';
    formModal.classList.remove('hidden');
  }

  async function openEdit(id) {
    const res = await fetch(`/api/tasks/${id}`);
    const t   = await res.json();

    form.id.value      = t.id;
    projSel.value      = t.projectId || '';
    titleInp.value     = t.title;
    descInp.value      = t.description || '';
    asgInp.value       = t.assignee   || '';
    prioSel.value      = t.priority;
    statusSel.value    = t.status;

    // dueDate → datetime-local 포맷 ("YYYY-MM-DDThh:mm")
    if (t.dueDate) {
        form.dueDate.value = t.dueDate.slice(0,16).replace('T',' ')
    } else {
      form.dueDate.value = '';
    }

    document.getElementById('form-title').textContent = '업무 수정';
    formModal.classList.remove('hidden');
  }

  async function openView(id) {
    currentViewId = id;
    const res = await fetch(`/api/tasks/${id}`);
    const t   = await res.json();

    // 프로젝트명이 있으면 "[프로젝트명] - 제목" 으로
    viewTitle.textContent = t.projectName ? `${t.projectName} – ${t.title}` : t.title;
    viewDesc.textContent     = t.description || '-';
    viewAsg.textContent      = t.assignee   || '-';
    viewPrio.textContent     = mapPriority(t.priority);
    viewDue.textContent      = t.dueDate
                                ? new Date(t.dueDate).toLocaleString()
                                : '-';
    viewStatus.textContent   = mapStatus(t.status);
    viewCreated.textContent  = new Date(t.createdAt).toLocaleString();
    viewUpdated.textContent  = new Date(t.updatedAt).toLocaleString();

    formModal.classList.add('hidden');
    viewModal.classList.remove('hidden');
  }

  async function saveTask() {
    const data = {
      id:          form.id.value || null,
      projectId:   form.projectId.value || null,
      title:       titleInp.value,
      description: descInp.value,
      assignee:    asgInp.value,
      priority:    prioSel.value,
      dueDate:     dueInp.value || null,
      status:      statusSel.value
    };
    const method = data.id ? 'PUT' : 'POST';
    const url    = data.id ? `/api/tasks/${data.id}` : '/api/tasks';

    await fetch(url, {
      method,
      headers:{ 'Content-Type':'application/json' },
      body: JSON.stringify(data)
    });

    formModal.classList.add('hidden');
    loadTasks();
  }

  async function deleteTask() {
    if (!confirm('정말 이 업무를 삭제하시겠습니까?')) return;
    await fetch(`/api/tasks/${currentViewId}`, { method:'DELETE' });
    viewModal.classList.add('hidden');
    loadTasks();
  }
});
