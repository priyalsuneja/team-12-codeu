/**
 * http://usejsdoc.org/
 */
function requestTranslation() {
   const text = document.getElementById('text').innerHTML;
   const languageCode = "zh";

   const label = document.getElementById('text');
   const params = new URLSearchParams();
   params.append('text', text);
   params.append('languageCode', languageCode);
        
   fetch('/translate', {
     method: 'POST',
     body: params
   }).then(response => response.text())
     .then((translatedMessage) => {
     label.innerText = translatedMessage;
   });
}