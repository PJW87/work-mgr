// flatpickr 초기화
flatpickr("#calendar", {
  inline: true,
  onChange: (selectedDates, dateStr) => {
    loadList(dateStr);
  }
});

// 일정 불러오기
function loadList(date) {
  fetch('/api/schedules?date=' + date)
    .then(res => res.json())
    .then(data => {
      const ul = document.getElementById('schedule-list');
      ul.innerHTML = '';
      if (data.length === 0) {
        ul.innerHTML = '<li>해당 날짜에 일정이 없습니다.</li>';
        return;
      }
      data.forEach(item => {
        const li = document.createElement('li');
        li.innerHTML =
          `<span class="date">${item.time}</span> ${item.title}`;
        ul.appendChild(li);
      });
    });
}

// 새 일정 추가
document.getElementById('btn-add-schedule')
  .addEventListener('click', () => {
    const date = document.getElementById('calendar').value;
    const title = prompt('일정 내용을 입력하세요');
    if (!date || !title) return;
    fetch('/api/schedules', {
      method: 'POST',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify({ date, title, time: '' })
    })
    .then(_ => loadList(date));
});

// 페이지 로드시 오늘 일정 자동 로드
document.addEventListener('DOMContentLoaded', () => {
  const today = new Date().toISOString().slice(0,10);
  loadList(today);
});
