document.addEventListener('DOMContentLoaded', () => {
  const listEl    = document.getElementById('issue-list');
  const pagerEl   = document.getElementById('pagination');
  const btnSearch = document.getElementById('btn-search');
  const btnNew    = document.getElementById('btn-new-issue');

  let page = /*[[${result.page}]]*/0;
  let size = /*[[${result.size}]]*/20;

  const sType = document.getElementById('search-type');
  const sKey  = document.getElementById('search-keyword');
  const sFrom = document.getElementById('search-from');
  const sTo   = document.getElementById('search-to');

  btnSearch.onclick = () => {
  // 1) 기간이 하나만 입력되었을 때
    if ((sFrom.value && !sTo.value) || (!sFrom.value && sTo.value)) {
      return alert('기간을 시작일과 종료일 모두 입력해주세요.');
    }
    // 2) 기간이 둘 다 입력됐지만 to < from 일 때
    if (sFrom.value && sTo.value && sTo.value < sFrom.value) {
      return alert('종료일은 시작일보다 이후여야 합니다.');
    }
    page = 0;
    loadIssues();
  };
  btnNew.onclick = () => {
    window.location.href = '/issues/new';
  };

  async function loadIssues() {
    // URLSearchParams 직접 생성
    const params = new URLSearchParams();
    params.set('type',    sType.value);
    params.set('keyword', sKey.value.trim());
    // 기간 필터: 둘 다 값 있을 때만
    if (sFrom.value && sTo.value) {
      params.set('from', sFrom.value);
      params.set('to',   sTo.value);
    }
    params.set('page', page);
    params.set('size', size);

    const res = await fetch(`/api/issues?${params}`);
    if (!res.ok) {
      console.error(await res.text());
      return;
    }
    const { data, page:cur, size:sz, total } = await res.json();
    renderTable(data);
    renderPager(cur, sz, total);
  }

  function renderTable(arr) {
    listEl.innerHTML = arr.map(i => `
      <tr data-id="${i.id}">
        <td><input type="checkbox" class="chk-item" data-id="${i.id}"/></td>
        <td>${i.id}</td>
         <td>
            <a href="/issues/${i.id}" class="post-link">
              ${i.title}
            </a>
         </td>
        <td>${i.author}</td>
        <td class="cell-status">
                       <span class="status-badge status-${i.status}">${translateStatus(i.status)}</span>
                     </td>
        <td>${i.createdAt.slice(0,10)}</td>
      </tr>
    `).join('');
      bindBatchDelete();
      bindSelectAll();
  }

  function renderPager(cur, sz, total) {
    const pages = Math.ceil(total / sz);
    pagerEl.innerHTML = '';
    for (let i = 0; i < pages; i++) {
      const b = document.createElement('button');
      b.textContent = i+1;
      b.disabled   = (i === cur);
      b.onclick    = () => { page = i; loadIssues(); };
      pagerEl.append(b);
    }
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
      fetch(`/api/issues/${id}`, { method:'DELETE' })
    ));
    loadIssues();
  };
}
  // 초기 로드
  loadIssues();
});
