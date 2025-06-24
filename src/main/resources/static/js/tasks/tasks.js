document.addEventListener('DOMContentLoaded', () => {
  const listEl       = document.getElementById('task-list');
  const btnNew       = document.getElementById('btn-new-task');
  const formModal    = document.getElementById('modal-task-form');
  const viewModal    = document.getElementById('modal-task-view');

  // form elements
  const form         = document.getElementById('task-form');
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
  document.getElementById('btn-edit-task').onclick  = () => openEdit(currentViewId);
  document.getElementById('btn-delete-task').onclick= deleteTask;

  let currentViewId = null;

  btnNew.onclick = () => openNew();

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

  async function loadTasks() {
    const res = await fetch('/api/tasks');
    const arr = await res.json();

    // 1) 우선순위 순, 2) dueDate 순 정렬
    arr.sort((a, b) => {
      const order = ['CRITICAL','HIGH','MEDIUM','LOW'];
      const pa = order.indexOf(a.priority), pb = order.indexOf(b.priority);
      if (pa !== pb) return pa - pb;
      if (a.dueDate && b.dueDate)  return new Date(a.dueDate) - new Date(b.dueDate);
      if (a.dueDate) return -1;
      if (b.dueDate) return 1;
      return 0;
    });

    listEl.innerHTML = arr.map(t => `
      <tr data-id="${t.id}">
        <td>${mapPriority(t.priority)}</td>
        <td class="link">${t.title}</td>
        <td>${t.assignee||'-'}</td>
        <td>${t.dueDate
              ? new Date(t.dueDate)
                  .toISOString()
                  .slice(0,16)
                  .replace('T',' ')
              : '-'}</td>
        <td>${mapStatus(t.status)}</td>
        <td>
          <button class="btn btn-secondary btn-sm edit">수정</button>
          <button class="btn btn-danger btn-sm del">삭제</button>
        </td>
      </tr>
    `).join('');

    attachRowHandlers();
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
    formModal.classList.remove('hidden');
  }

  async function openEdit(id) {
    const res = await fetch(`/api/tasks/${id}`);
    const t   = await res.json();

    form.id.value      = t.id;
    titleInp.value     = t.title;
    descInp.value      = t.description || '';
    asgInp.value       = t.assignee   || '';
    prioSel.value      = t.priority;
    statusSel.value    = t.status;

    // dueDate → datetime-local 포맷 ("YYYY-MM-DDThh:mm")
    if (t.dueDate) {
      const dt = new Date(t.dueDate);
      form.dueDate.value = dt.toISOString().slice(0,16);
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

    viewTitle.textContent    = t.title;
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
