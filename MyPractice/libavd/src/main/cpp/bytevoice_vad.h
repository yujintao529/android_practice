#ifndef bytevoice_vad_h
#define bytevoice_vad_h

#include "bytevoice_types.h"

class BvCnnVadInst {
public:
    BvCnnVadInst() = default;

    virtual ~BvCnnVadInst() = default;

    // mode: 1: most strict mode for speech; 0: normal mode; -1: lest strict mode for speech
    virtual int Init(int mode, int fs) = 0;

    virtual int Reset() = 0;

    virtual void Destroy() = 0;

    virtual float Process(const int16_t *input, int16_t frm_size, bool do_smooth) = 0;
};


class BvActSpkDtctorInst {
public:
    BvActSpkDtctorInst() = default;

    virtual ~BvActSpkDtctorInst() = default;

    virtual int Reset() = 0;

    virtual bool Process(const int16_t *input, int frm_len) = 0;
};

#endif /* bytevoice_vad_h */
