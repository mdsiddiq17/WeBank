/**
 * 
 */
document.addEventListener('DOMContentLoaded', function () {
    const tabs = document.querySelectorAll('.tabs button');
    const tabContents = document.querySelectorAll('.tab-content');

    tabs.forEach(tab => {
        tab.addEventListener('click', function () {
            tabs.forEach(btn => btn.classList.remove('active'));
            tabContents.forEach(content => content.classList.remove('active'));

            tab.classList.add('active');
            document.querySelector(`#${tab.dataset.tab}`).classList.add('active');
        });
    });
});

document.addEventListener('DOMContentLoaded', function() {
    const navItems = document.querySelectorAll('.nav-item a');

    navItems.forEach(item => {
        item.addEventListener('click', function() {
            // Remove active class from all items
            navItems.forEach(nav => nav.classList.remove('active'));

            // Add active class to the clicked item
            this.classList.add('active');
        });
    });
});



///
const body=document.querySelector("body"),
        modeToggle = body.querySelector(".mode-toggle");

modeToggle.addEventListener("click",()=>{
    body.classList.toggle("dark");
})