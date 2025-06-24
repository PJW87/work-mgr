// src/main/resources/static/js/schedule/schedule.js
document.addEventListener('DOMContentLoaded', () => {
  const btnNew      = document.getElementById('btn-new');
  const modalEdit   = document.getElementById('modal-schedule');
  const form        = document.getElementById('form-schedule');
  const saveBtn     = document.getElementById('save-sched');
  const cancelBtn   = document.getElementById('cancel-sched');
  const upcoming    = document.getElementById('upcoming-list');

  const modalView   = document.getElementById('modal-view');
  const btnCloseView= document.getElementById('btn-close-view');
  const btnEditView = document.getElementById('btn-edit');
  const viewTitle   = document.getElementById('view-title');
  const viewContent = document.getElementById('view-content');
  const viewStart   = document.getElementById('view-start');
  const viewEnd     = document.getElementById('view-end');
  const btnDeleteView = document.getElementById('delete-sched'); // 삭제 버튼
  // FullCalendar 초기화
  const calendar = new FullCalendar.Calendar(
    document.getElementById('calendar'), {
      initialView: 'dayGridMonth',
      locale: 'ko',
      height: 600,
      headerToolbar: {
        left: 'prev,next today',
        center: 'title',
        right: 'dayGridMonth,timeGridWeek,timeGridDay'
      },
      events: fetchAllEvents,
      dateClick: info => openEditModal({ startDate: info.dateStr }),
      eventClick: info => openViewModal(info.event.id)
  });
  calendar.render();

 // 2) 오른쪽 “최근 일정” 로드
  async function loadUpcoming() {
    const res = await fetch('/api/schedules/upcoming');
    const list = await res.json();
    const today = new Date().toISOString().slice(0,10);
    // 카드 스타일로 렌더링
    const container = document.getElementById('upcoming-list');
    container.innerHTML = list.map(s => {
                  const date    = s.startDate.slice(0,10);
                  const cls     = date < today
                    ? 'past'
                    : date === today
                      ? 'today'
                      : '';
                  return `
                    <li>
                      <div class="upcoming-card ${cls}" data-id="${s.id}">
                        <span class="uc-title">${s.title}</span>
                        <span class="uc-date">${date}</span>
                      </div>
                    </li>
                  `;
                }).join('');
    // 클릭 시 상세 모달
    document.querySelectorAll('.upcoming-card')
      .forEach(li => li.onclick = () => openViewModal(li.dataset.id));
  }
  loadUpcoming();

  // 새 일정 버튼
  btnNew.onclick = () => openEditModal();

  cancelBtn.onclick = () => modalEdit.classList.add('hidden');
  saveBtn.onclick   = async () => {
    const data = {
      id:        form.id.value || null,
      title:     form.title.value,
      content:   form.content.value,
      startDate: form.startDate.value,
      endDate:   form.endDate.value || null
    };
    const method = data.id ? 'PUT' : 'POST';
    const url    = data.id ? `/api/schedules/${data.id}` : '/api/schedules';
    await fetch(url, {
      method,
      headers: { 'Content-Type':'application/json' },
      body: JSON.stringify(data)
    });
    modalEdit.classList.add('hidden');
    calendar.refetchEvents();
    loadUpcoming();
  };

  // 이벤트 소스
  async function fetchAllEvents(info, successCallback) {
    const res = await fetch('/api/schedules');
    const arr = await res.json();
    successCallback(arr.map(s => ({
      id:    s.id,
      title: s.title,
      start: s.startDate,
      end:   s.endDate
    })));
  }

  // --- 상세보기 모달 열기 ---
  async function openViewModal(id) {
    const res = await fetch(`/api/schedules/${id}`);
    const s   = await res.json();
    viewTitle.textContent   = s.title;
    viewContent.textContent = s.content || '-';
    viewStart.textContent   = s.startDate.replace('T',' ');
    viewEnd.textContent     = s.endDate   ? s.endDate.replace('T',' ') : '-';
    btnEditView.onclick     = () => {
      modalView.classList.add('hidden');
      openEditModal(s);
    };
     // 5) 삭제 버튼 클릭 시,
      btnDeleteView.onclick = async () => {
        if (!confirm('정말 이 일정을 삭제하시겠습니까?')) return;
        const delRes = await fetch(`/api/schedules/${id}`, { method: 'DELETE' });
        if (!delRes.ok) {
          alert('삭제에 실패했습니다.');
          return;
        }
        modalView.classList.add('hidden');
        calendar.refetchEvents();  // FullCalendar 갱신
        loadUpcoming();            // 최근 일정 갱신
      };

    modalView.classList.remove('hidden');
  }

  btnCloseView.onclick = () => modalView.classList.add('hidden');

  // --- 등록/수정 모달 열기 ---
  function openEditModal(data = {}) {
    form.reset();
    Object.entries(data).forEach(([k,v])=>{
      if (form[k]) form[k].value = v || '';
    });
    document.getElementById('modal-title')
      .textContent = data.id ? '일정 수정' : '새 일정';
    modalEdit.classList.remove('hidden');
  }
});
