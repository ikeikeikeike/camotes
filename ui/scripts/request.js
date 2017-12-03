$(() => {  if ( !!!$('#req').length ) return;
  const $request = $('#req');
        $form = $request.find('form'),
        $submit = $request.find('form .req-submit'),
        $result = $request.find('.req-result');

  const elseUnknown = (value) => {
    if (!value) {
      return ''
    } else if (value == 'unknown') {
      return ''
    } else {
      return value;
    }
  }

  const myround = (number, d) => {
    if (!number) return '';

    d = d || 2;
    return Math.floor(number * Math.pow(10, d)) / Math.pow(10, d);
  }

  const renderResult = ($form, cb) => {
    if (! validateURL($form.find('[name="src"]').val()))
      return alert('Invalid URL');

    $.ajax({ method: 'POST', url: $form.attr('action'), data: $form.serialize(), dataType: 'json', cache: true }).done(resps => {

      $result.find('*').remove()

      const $html = resps.map(resp => {
        const $div = $(`
        <div class='res-meta'>
          <div class='res-title'>title: ${resp.title}</div>
          <div class='res-content'>content: ${resp.content}</div>
          <div class='res-formats'>
            <table class="table">
              <caption>Optional table caption.</caption>
              <thead>
                <tr>
                  <th>#</th>
                  <th>Name</th>
                  <th>Format</th>
                  <th>Resolution</th>
                  <th>Bitrate</th>
                </tr>
              </thead>

              <tbody><!--this one--></tbody>

            </table>
          </div>
        </div>
        `);

        resp.formats.forEach(fmt => { $div.find('.res-formats tbody').append(`
        <tr class='res-format'>
          <th scope="row"><a class='btn btn-xs btn-default res-donwload'
            data-manifestUrl='${fmt.manifestUrl}'
            data-ext='${fmt.ext}'
            data-url='${fmt.url}'
            data-protocol='${fmt.protocol}'
            data-format='${fmt.format}'
            data-formatId='${fmt.formatId}'
            >
            Download</a>
          </th>
          <td>${elseUnknown(fmt.format)}</td>
          <td>${elseUnknown(fmt.ext)}</td>
          <td>${elseUnknown(fmt.resolution)}</td>
          <td>${myround(elseUnknown(fmt.tbr) || '', 2)}</td>
        </tr>
        `)});

        return $div
      })

      $result.append($html)

      if (cb) cb(resps);
    })
  };

  $submit.on('click', (ev) => { renderResult($form, (resps) => { $result.find('.res-donwload').on('click', (ev) => {
    console.log($(ev.currentTarget).data())
  })})});

  $form.on("keypress", (ev) => {
    switch (ev.keyCode ? ev.keyCode : ev.which) {
    case 13:
      ev.preventDefault(); ev.stopPropagation();
      $submit.trigger('click');
    }
  });






  const validateURL = (value) =>
    /^(https?|ftp):\/\/(((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:)*@)?(((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5]))|((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?)(:\d*)?)(\/((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)+(\/(([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)*)*)?)?(\?((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|[\uE000-\uF8FF]|\/|\?)*)?(\#((([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(%[\da-f]{2})|[!\$&'\(\)\*\+,;=]|:|@)|\/|\?)*)?$/i.test(value);
