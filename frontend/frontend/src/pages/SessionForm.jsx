import React from 'react';

const SessionForm = ({ onSubmit, initialValues }) => {
  return (
    <form onSubmit={onSubmit}>
      <div>
        <label htmlFor="title">Title:</label>
        <input
          type="text"
          id="title"
          name="title"
          defaultValue={initialValues.title}
          required
        />
      </div>
      <div>
        <label htmlFor="content">Content:</label>
        <textarea
          id="content"
          name="content"
          defaultValue={initialValues.content}
          required
          style={{
            height: '100px', // Increase default height by 20%
            resize: 'vertical' // Allow vertical resizing
          }}
        />
      </div>
      <button type="submit">Submit</button>
    </form>
  );
};

export default SessionForm;
