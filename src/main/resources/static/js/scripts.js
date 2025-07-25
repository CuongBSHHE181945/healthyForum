/*!
* Start Bootstrap - Small Business v5.0.6 (https://startbootstrap.com/template/small-business)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-small-business/blob/master/LICENSE)
*/
// This file is intentionally blank
// Use this file to add JavaScript to your project

// Unified Search Bar Functionality
(function() {
  const searchBar = document.getElementById('searchBar');
  const searchButton = document.getElementById('searchButton');
  const resultsDropdown = document.getElementById('searchResultsDropdown');
  const errorMsg = document.getElementById('searchErrorMsg');
  const noResultsMsg = document.getElementById('searchNoResultsMsg');
  let debounceTimeout = null;

  function clearDropdown() {
    resultsDropdown.innerHTML = '';
    resultsDropdown.style.display = 'none';
    resultsDropdown.classList.add('d-none');
  }

  function showDropdown() {
    resultsDropdown.classList.remove('d-none');
    resultsDropdown.style.display = 'block';
  }

  function hideMessages() {
    errorMsg.classList.add('d-none');
    noResultsMsg.classList.add('d-none');
  }

  function showError(message) {
    errorMsg.textContent = message;
    errorMsg.classList.remove('d-none');
    clearDropdown();
  }

  function showNoResults() {
    noResultsMsg.textContent = 'No posts or users found.';
    noResultsMsg.classList.remove('d-none');
    clearDropdown();
  }

  function renderResults(data) {
    let html = '';
    let hasResults = false;
    if (data.posts && data.posts.length > 0) {
      hasResults = true;
      html += '<h6 class="dropdown-header">Posts</h6>';
      data.posts.forEach(post => {
        html += `<a class="dropdown-item" href="${post.link}"><strong>${post.title}</strong><br><small>${post.snippet}</small><br><span class='text-muted'>by ${post.userDisplayName}</span></a>`;
      });
    }
    if (data.users && data.users.length > 0) {
      hasResults = true;
      html += '<h6 class="dropdown-header mt-2">Users</h6>';
      data.users.forEach(user => {
        html += `<a class="dropdown-item" href="${user.link}"><span class="me-2"><i class="bi bi-person-circle"></i></span>${user.displayName} <small class='text-muted'>(${user.username || ''})</small></a>`;
      });
    }
    if (hasResults) {
      resultsDropdown.innerHTML = html;
      showDropdown();
    } else {
      showNoResults();
    }
  }

  function doSearch() {
    const query = searchBar.value.trim();
    hideMessages();
    if (!query) {
      clearDropdown();
      return;
    }
    fetch(`/search?q=${encodeURIComponent(query)}`)
      .then(res => res.json())
      .then(data => {
        if (data.error) {
          showError(data.error);
        } else {
          renderResults(data);
        }
      })
      .catch(() => {
        showError('A network or server error occurred.');
      });
  }

  // Debounced input handler
  searchBar.addEventListener('input', function() {
    hideMessages();
    clearDropdown();
    if (debounceTimeout) clearTimeout(debounceTimeout);
    debounceTimeout = setTimeout(doSearch, 300);
  });

  // Optional: search on button click
  if (searchButton) {
    searchButton.addEventListener('click', function(e) {
      e.preventDefault();
      doSearch();
    });
  }

  // Hide dropdown when clicking outside
  document.addEventListener('click', function(e) {
    if (!resultsDropdown.contains(e.target) && e.target !== searchBar) {
      clearDropdown();
    }
  });

  // Show dropdown on focus if results exist
  searchBar.addEventListener('focus', function() {
    if (resultsDropdown.innerHTML.trim() !== '') {
      showDropdown();
    }
  });
})();

// --- FEED AJAX LOGIC ---
document.addEventListener('DOMContentLoaded', function() {
  const feed = document.getElementById('feed');
  const networkErrorMsg = document.getElementById('networkErrorMsg');
  const filterButtons = document.querySelectorAll('.btn-outline-primary');

  // Helper to show/hide network error
  function showNetworkError(show) {
    if (networkErrorMsg) {
      networkErrorMsg.classList.toggle('d-none', !show);
    }
  }

  // AJAX load for filter buttons
  filterButtons.forEach(btn => {
    btn.addEventListener('click', function(e) {
      e.preventDefault();
      const url = btn.getAttribute('href');
      fetch(url)
        .then(res => {
          if (!res.ok) throw new Error('Network');
          return res.text();
        })
        .then(html => {
          const parser = new DOMParser();
          const doc = parser.parseFromString(html, 'text/html');
          const newFeed = doc.getElementById('feed');
          if (newFeed && feed) {
            feed.innerHTML = newFeed.innerHTML;
          }
          showNetworkError(false);
        })
        .catch(() => {
          showNetworkError(true);
        });
    });
  });

  // AJAX load for 'Load More' button
  if (feed) {
    feed.addEventListener('submit', function(e) {
      if (e.target.matches('form')) {
        e.preventDefault();
        const form = e.target;
        const url = form.getAttribute('action') + '?' + new URLSearchParams(new FormData(form)).toString();
        fetch(url)
          .then(res => {
            if (!res.ok) throw new Error('Network');
            return res.text();
          })
          .then(html => {
            const parser = new DOMParser();
            const doc = parser.parseFromString(html, 'text/html');
            const newFeed = doc.getElementById('feed');
            if (newFeed && feed) {
              // Append new posts
              feed.innerHTML += newFeed.innerHTML;
            }
            showNetworkError(false);
          })
          .catch(() => {
            showNetworkError(true);
          });
      }
    });
  }
});