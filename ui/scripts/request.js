$(() => {  if ( !!!$('#req').length ) return;
  const $request = $('#req');
        $form = $request.find('form'),
        $submit = $request.find('form .req-submit'),
        $result = $request.find('.req-result');

  const renderResult = ($form, cb) => {
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
                  <th>ext</th>
                  <th>resolution</th>
                  <th>bit rate</th>
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
          <td>${fmt.ext}</td>
          <td>${fmt.resolution}</td>
          <td>${fmt.tbr}</td>
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

});
